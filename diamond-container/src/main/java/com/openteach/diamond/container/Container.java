/**
 * Copyright 2013 Qiangqiang RAO
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 */
package com.openteach.diamond.container;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.osgi.framework.internal.core.FrameworkProperties;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * 
 * @author sihai
 *
 */
public class Container {

	private static final Log logger = LogFactory.getLog(Container.class);
	
	private static final String BUNDLE_PROPERTY_FILE = "diamond_bundle.properties";
	
	private static final String CONTAINERCALLBACK_BUNDLE = "diamond.log.callback";
	
	private static final String CALLBACK_SERVICE = "com.taobao.diamond.log.CallbackService";
	
	private static final String CALLBACK_COMPONENT = "com.taobao.diamond.log.impl.CallbackComponent";
	
	private static final String diamond_LOGPATH_KEY = "diamond.LOG.PATH";
	
	private static BundleContext context = null;
	
	private static final String DIAMOND_LIFECYCLE_FILE = "diamond_lifecycle_file.properties";
	
    private static final String  EXCLUDED_FILE     = "excluded.properties";
    
    private static final String EXCLUDED_PREFIX_PROP = "com.galaxy.diamond.container.excluded.prefixes";
    
	private static List<LifecycleListener> lifecycleListeners = new ArrayList<LifecycleListener>();
	
	static {
		initLifecycleListeners();
	}
	
	@SuppressWarnings("unchecked")
	private static void initLifecycleListeners() {
		try {
			Enumeration<URL> cUrls = Container.class.getClassLoader().getResources(DIAMOND_LIFECYCLE_FILE);
			while(cUrls.hasMoreElements()){
				URL url = cUrls.nextElement();
				BufferedReader br = null;
				try {
					br = new BufferedReader(new InputStreamReader(url.openStream()));
					String line = null;
					while((line = br.readLine()) != null){
						try {
							Class clazz = Container.class.getClassLoader().loadClass(line.trim());
							if(LifecycleListener.class.isAssignableFrom(clazz)){
								lifecycleListeners.add((LifecycleListener) clazz.newInstance());
							}
						} catch (ClassNotFoundException e) {
							System.err.println("找不到生命周期定义类："+line);
							e.printStackTrace();
						}
						//默认只读第一行
						break;
					}
				} catch (IOException e) {
					logger.error(e);
				} finally {
					if(null != br) {
						br.close();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e) {
			System.err.println("获取diamond生命周期文件出错!  错误原因："+e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void fireLifecycleEvent(String eventType) throws Exception{
		for(LifecycleListener listener : lifecycleListeners){
			try {
				listener.fire(eventType);
			} catch (RuntimeException e) {	//runtime异常不抛出去
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 启动并运行diamond容器
	 */
	public static void start(String[] args) throws Exception{
		//生命周期事件
		fireLifecycleEvent(LifecycleListener.BEFORE_START_EVENT);
		//设置外部加载类
		setExtenalClass();
		//start OSGI
		startOSGI(args);
		//生命周期事件
		fireLifecycleEvent(LifecycleListener.AFTER_START_EVENT);
	}

	private static void setExtenalClass() {
		// 获取需要优先从外部容器加载的类配置
        // 1.如果在系统环境变量中配置过, 则直接跳过
        String excluded = System.getProperty(EXCLUDED_PREFIX_PROP);
        if (excluded == null) {
            //2.从系统环境变量中查找配置文件名, 如果找到则从这个文件中加载配置, 这个文件需要打包
            //  在diamond.container中
            String excludedPropertiesFile = System.getProperty("com.galaxy.diamond.container.excluded.prefixes.file");
            //3.在系统环境变量中没有找到配置文件名, 则从默认的配置文件中加载
            if (excludedPropertiesFile == null) {
                excludedPropertiesFile = EXCLUDED_FILE;
            }
            
            InputStream is = null;
            Properties properties = new Properties();
            try {
                is = Container.class.getClassLoader().getResourceAsStream(excludedPropertiesFile);
				if (is != null) {
					properties.load(is);
					is.close();
				}
            } catch (IOException e) {
                // 配置文件有可能不存在, 或读取失败
                properties.clear();
            } finally {
            	if(null != is) {
            		try {
            			is.close();
            		} catch (IOException e) {
            			// 
            			logger.error(e);
            		}
            	}
            }
            //5.如果在所有的配置文件及环境变量中都没有找到, 此时exclude为null
            excluded = properties.getProperty(EXCLUDED_PREFIX_PROP);
            if (excluded != null) {
                System.setProperty(EXCLUDED_PREFIX_PROP, excluded);
            }
        }
	}

	private static void startOSGI(String[] args) throws Exception {
		String osgiBundlesBuilder = buildOSGIBundles(args);
		// 配置Equinox的启动
		FrameworkProperties.setProperty("osgi.noShutdown", "true");
		FrameworkProperties.setProperty("eclipse.ignoreApp", "true");
		FrameworkProperties.setProperty("osgi.bundles.defaultStartLevel", "4");
		FrameworkProperties.setProperty("osgi.bundles", osgiBundlesBuilder);
		// 在equinox已配置的hook上再增加diamondClassLoaderDelegateHook
		FrameworkProperties.setProperty("osgi.hook.configurators", "org.eclipse.osgi.internal.baseadaptor.BaseHookConfigurator," +
				"org.eclipse.osgi.internal.baseadaptor.DevClassLoadingHook," +
				"org.eclipse.core.runtime.internal.adaptor.EclipseStorageHook," +
				"org.eclipse.core.runtime.internal.adaptor.EclipseLogHook," +
				"org.eclipse.core.runtime.internal.adaptor.EclipseErrorHandler," +
				"org.eclipse.core.runtime.internal.adaptor.EclipseAdaptorHook," +
				"org.eclipse.core.runtime.internal.adaptor.EclipseClassLoadingHook," +
				"org.eclipse.core.runtime.internal.adaptor.EclipseLazyStarter," +
				"org.eclipse.core.runtime.internal.stats.StatsManager," +
				"org.eclipse.osgi.internal.signedcontent.SignedBundleHook," +
				"com.galaxy.diamond.container.diamondClassLoaderDelegateHook");
		FrameworkProperties.setProperty("builtin.hooks", "true");
		// 调用EclipseStarter，完成容器的启动
		// 每次启动时清空configuration，避免由于Equinox保存了Bundle的状态，带来重启后都是旧的东西的问题
		File configDir = new File("diamond.configuration");
		if(configDir.exists()){
			deleteDir(configDir);
		}
		EclipseStarter.run(new String[]{"-configuration", "diamond.configuration"}, null);
		// 通过EclipeStarter获取到BundleContext
		context = EclipseStarter.getSystemBundleContext();
		initCallbackService();
	}

	private static String buildOSGIBundles(String[] args) throws Exception {
		// 扫描当前classloader根路径下plugins目录中的所有jar，并组装为需要装载的插件
		URL url;
		if (args == null || args.length == 0) {
			url = Container.class.getClassLoader().getResource("");
		} else {
			url = new File(args[0]).toURI().toURL();
		}
		String path = url.getPath();
		System.setProperty("diamond.CONTAINER.PATH", path);
		String searchPath = path + "/plugins";
		File file = new File(searchPath);
		if(!file.exists()){
			throw new RuntimeException("未找到插件所在的plugins目录，目录地址为：" + searchPath);
		}
		StringBuilder osgiBundlesBuilder = new StringBuilder();
		//build core
		buildBundlesString(searchPath, "core", osgiBundlesBuilder, true);
		//build extra
		buildBundlesString(searchPath, "extra", osgiBundlesBuilder, false);
		//build option
		buildBundlesString(searchPath, "option", osgiBundlesBuilder, false);
		//build custom
		buildBundlesString(searchPath, "custom", osgiBundlesBuilder, false);
		return osgiBundlesBuilder.toString();
	}
	
	private static void buildBundlesString(String parent , String child ,StringBuilder sb, boolean first) {
		File file = new File(parent,child);
		if(file.exists() ) {
			String filePath = file.getAbsolutePath();
			String[] pluginFiles = file.list(new FilenameFilter() {
				
				public boolean accept(File dir, String name) {
					if(name.endsWith(".plugin")){
						return true;
					}
					return false;
				}
			});
			StringBuilder osgiBundlesBuilder = new StringBuilder();
			int pluginFilesSize = pluginFiles.length;
			if(pluginFilesSize > 0) {
				osgiBundlesBuilder.append(filePath + File.separator + pluginFiles[0]);
				osgiBundlesBuilder.append("@start");
			}
			for (int i = 1; i < pluginFilesSize; i++) {
				osgiBundlesBuilder.append(",");
				osgiBundlesBuilder.append(filePath + File.separator + pluginFiles[i]);
				osgiBundlesBuilder.append("@start");
			}
			if(first) {
				sb.append(osgiBundlesBuilder.toString());
			} else {
				sb.append("," + osgiBundlesBuilder.toString());
			}
		} else {
			if(first) {
				throw new RuntimeException("diamond运行必须的插件包不存在，错误原因： 目录 " + parent + " 下面不存在" + child + "目录!");
			}
		}
	}

	/**
	 * 停止diamond容器
	 */
	public static void stop() {
		try {
			fireLifecycleEvent(LifecycleListener.BEFORE_STOP_EVENT);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		try {
			EclipseStarter.shutdown();
			context = null;
		} catch (Exception e) {
			System.err.println("停止diamond容器时出现错误:"+e);
			e.printStackTrace();
		}
		try {
			fireLifecycleEvent(LifecycleListener.AFTER_STOP_EVENT);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 从diamond容器中获取OSGi服务instance
	 * 
	 * @param serviceName 服务名称（完整接口类名）
	 * 
	 * @return Object 当找不到对应的服务时返回null
	 */
	public static Object getOSGiService(String serviceName){
		ServiceReference ref = context.getServiceReference(serviceName);
		if(null == ref)
			return null;
		return context.getService(ref);
	}
	
	/**
	 * 获取需要对外提供给第三方容器能够访问的类
	 * 
	 * @return Map<String, Class<?>> key: 完整类名  Class<?> Class对象
	 */
	public static Map<String, Class<?>> getExportedClasses() throws Exception {
		Map<String, Class<?>> exportedClasses = new HashMap<String, Class<?>>();
		Properties exportedClassesProps = new Properties();
		InputStream is = null;
		Bundle[] bundles = context.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			String bundleName = bundles[i].getSymbolicName().toLowerCase();
			URL url  = bundles[i].getResource(BUNDLE_PROPERTY_FILE);
			if(url != null) {
				is = url.openStream();
				exportedClassesProps.load(is);
				is.close();
			}
			
			if(exportedClassesProps.containsKey(bundleName)) {
				String value=exportedClassesProps.getProperty(bundleName);
				String[] classes=value.split(",");
				for (int j = 0; j < classes.length; j++) {
					exportedClasses.put(classes[j], bundles[i].loadClass(classes[j].trim()));
				}
			}
		}
		return exportedClasses;
	}
	
	/**
	 * 包权限
	 */
	static ClassLoader getBundleClassLoader(String bundleName, String className){
		Bundle[] bundles = context.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			String symbolicName = bundles[i].getSymbolicName().toLowerCase();
			if(symbolicName.equals(bundleName)){
				try {
					Class<?> c = bundles[i].loadClass(className);
					if(c != null){
						return c.getClassLoader();
					}
				} catch (ClassNotFoundException e) {
					return null;
				}
			}
		}
		return null;
	}
	
	public static void addThirdContainerClassLoader(ClassLoader loader){
		DefaultClassLoaderDelegateHook.addThirdClassLoader(loader);
	}
	
	/**
	 * 便于等待应用启动后再来进行回调，初始化一些东西，例如log地址等
	 */
	public void callback(){
		initLogPath();
	}
	
	private static void initCallbackService() throws Exception {
		Bundle[] bundles = context.getBundles();
		Object callbackComponent = null;
		for (int i = 0; i < bundles.length; i++) {
			String bundleName = bundles[i].getSymbolicName().toLowerCase();
			if(bundleName.equals(CONTAINERCALLBACK_BUNDLE)){
				callbackComponent = bundles[i].loadClass(CALLBACK_COMPONENT).newInstance();
				Method method = callbackComponent.getClass().getMethod("setdiamondContainer", new Class<?>[]{Object.class});
				method.invoke(callbackComponent, new Object[]{new Container()});
				break;
			}
		}
		context.registerService(CALLBACK_SERVICE, callbackComponent, null);
	}
	
	private void initLogPath(){
		FileAppender bizFileAppender = getFileAppender(Logger.getRootLogger());
        if (null == bizFileAppender) {
        	if(System.getProperty(diamond_LOGPATH_KEY) == null){
        		 System.setProperty(diamond_LOGPATH_KEY, "");
        	}
            return;
        }
        String bizLogDir = new File(bizFileAppender.getFile()).getParent();
        if (bizLogDir != null) {
			System.setProperty(diamond_LOGPATH_KEY, bizLogDir);
		} else {
        	if(System.getProperty(diamond_LOGPATH_KEY) == null){
       		 	System.setProperty(diamond_LOGPATH_KEY, "");
        	}
		}
	}
	
	private FileAppender getFileAppender(Logger logger) {
        FileAppender fileAppender = null;
        for (Enumeration<?> appenders = logger.getAllAppenders();
                (null == fileAppender) && appenders.hasMoreElements();) {
            Appender appender = (Appender) appenders.nextElement();
            if (FileAppender.class.isInstance(appender)) {
                fileAppender = (FileAppender) appender;
            }
        }
        return fileAppender;
    }
	
	private static void deleteDir(File dir) {
	    if (dir == null || !dir.exists() || !dir.isDirectory())
	        return; 
	    for (File file: dir.listFiles()) {
	        if (file.isFile())
	            file.delete(); 
	        else if (file.isDirectory())
	            deleteDir(file); 
	    }
	    dir.delete();// 删除目录本身
	}
	
	public static void main(String[] args) {
		try {
			start(new String[]{"E:\\diamond"});
			Thread.sleep(60 * 1000);
			stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}