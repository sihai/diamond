/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.container.third.tomcat;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.naming.resources.DirContextURLStreamHandlerFactory;

/**
 * 描述：基于Tomcat LifecycleListener来实现SAR的部署
 * @author sihai
 *
 */
public class SARDeployer implements LifecycleListener {

	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		Lifecycle lifecycle = event.getLifecycle();
		if (Lifecycle.START_EVENT.equals(event.getType())) {
			if (lifecycle instanceof Server) {
				try {
					setURLStreamHandler();
					deploySars();
				} catch (Exception e) {
					System.err.println("部署SAR时出现问题！");
					e.printStackTrace();
				}
			}
		}
	}
	
	private void setURLStreamHandler() {
		java.net.URLStreamHandlerFactory streamHandlerFactory = new DirContextURLStreamHandlerFactory();
		URL.setURLStreamHandlerFactory(streamHandlerFactory);
	}
	
	/**
	 * 扫描部署所有的SAR
	 */
	private void deploySars() throws Exception {
		String searchDir = System.getProperty("catalina.home") + File.separator + "webapps";
		File file = new File(searchDir);
		File[] sars = file.listFiles(new FileFilter(){

			public boolean accept(File pathname) {
				if((pathname.isDirectory()) && (pathname.getName().endsWith(".sar"))){
					return true;
				}
				return false;
			}
			
		});
		if(sars.length>0){
			System.out.println("需要部署的SAR数量为："+sars.length);
		}
		for (int i = 0; i < sars.length; i++) {
			deploySar(sars[i].getCanonicalPath()+File.separator);
		}
	}
	
	/**
	 * 部署单个SAR<br/>
	 * 部署步骤：
	 * 1、获取path+META-INF/tomcat.properties，读取并得到启动类的类名
	 * 2、基于path+lib创建ClassLoader，ClassLoader的父classloader设置为当前classloader
	 * 3、反射调用启动类的start方法完成SAR的启动
	 */
	private void deploySar(String path) throws Exception{
		System.out.println("开始部署SAR，路径为：" + path);
		if(!path.endsWith(File.separator)){
			path += File.separator;
		}
		// Step 1
		String tomcatFileName = path + "META-INF" + File.separator + "tomcat.properties";
		File tomcatFile = new File(tomcatFileName);
		if(!tomcatFile.exists()){
			throw new FileNotFoundException("不符合SAR Deployer规范，未找到文件：" + tomcatFileName);
		}
		
		FileInputStream fin = null;
		Properties properties = new Properties();
		try {
			fin = new FileInputStream(tomcatFile);
			properties.load(fin);
		} finally {
			if(null != fin) {
				fin.close();
			}
		}
		fin.close();
		String startupClassName = properties.getProperty("classname");
		if((startupClassName == null) || ("".equals(startupClassName.trim()))){
			throw new IllegalArgumentException("不符合SAR Deployer规范，文件："+tomcatFileName+"中未配置classname属性");
		}
		// Step 2
		String jarFileInfo = path + "lib";
		File jarFileDir = new File(jarFileInfo);
		if(!jarFileDir.exists()){
			throw new FileNotFoundException("不符合SAR Deployer规范，未找到文件："+jarFileInfo);
		}
		File[] jarFiles = jarFileDir.listFiles(new FileFilter(){

			public boolean accept(File pathname) {
				if((pathname.isFile()) && (pathname.getName().endsWith(".jar"))){
					return true;
				}
				return false;
			}
			
		});
		URL[] jarURLs = new URL[jarFiles.length];
		for (int i = 0; i < jarFiles.length; i++) {
			jarURLs[i] = jarFiles[i].toURI().toURL();
		}
		ClassLoader sarLoader = new SARClassLoader(jarURLs, this.getClass().getClassLoader());
		HSFAppClassLoader.setSarClassLoader(sarLoader);
		// step 3
		Class<?> startupClass = sarLoader.loadClass(startupClassName);
		Object startupObject = startupClass.newInstance();
		Method method = startupClass.getDeclaredMethod("start", new Class<?>[]{String.class});
		method.invoke(startupObject, new Object[]{path});
		System.out.println("路径为：" + path + "的SAR部署完毕！");
	}
	
	class SARClassLoader extends URLClassLoader {

		public SARClassLoader(URL[] urls, ClassLoader parent) {
			super(urls, parent);
		}
	}
}
