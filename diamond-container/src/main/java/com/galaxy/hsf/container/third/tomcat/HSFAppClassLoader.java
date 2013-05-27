/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.container.third.tomcat;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.catalina.loader.WebappClassLoader;

/**
 * 描述：HSF应用的classloader，以便能够让应用加载到一些HSF容器中的类
 * @author sihai
 *
 */
public class HSFAppClassLoader extends WebappClassLoader {

	private static ClassLoader sarClassLoader = null;
	
	private Map<String, Class<?>> loadedClasses = new HashMap<String, Class<?>>();
	
	private AtomicBoolean initialized = new AtomicBoolean(false);	//
	
	public HSFAppClassLoader() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public HSFAppClassLoader(ClassLoader classloader) {
		super(classloader);
		try {
			// 确保HSF能加载到应用的类
			Class containerClass = sarClassLoader.loadClass("com.galaxy.hsf.container.HSFContainer");
			Method method = containerClass.getDeclaredMethod("addThirdContainerClassLoader", new Class<?>[]{ClassLoader.class});
			method.invoke(null, new Object[]{this});
		} catch (Exception e) {
			System.err.println("设置classloader到HSF容器中时出现错误！");
			e.printStackTrace();
		}
	}
	
	public static void setSarClassLoader(ClassLoader sarClassLoader) {
		HSFAppClassLoader.sarClassLoader = sarClassLoader;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if(initialized.compareAndSet(false, true)){
			// 判断加载的是否为有spring的应用，如不为则不去加载HSF的类，这个地方有点不够优雅，应该做出改进
			try{
				Class springClass = super.loadClass("org.springframework.beans.factory.FactoryBean", resolve);
				if(springClass == null){
					initialized.set(false);
					return super.loadClass(name,resolve);
				}
			} catch(Exception e){
				initialized.set(false);
				return super.loadClass(name,resolve);
			}
			// 确保应用能加载到HSF的类
			try{
				Class containerClass = sarClassLoader.loadClass("com.galaxy.hsf.container.HSFContainer");
				Method method = containerClass.getDeclaredMethod("getExportedClasses", new Class<?>[]{});
				loadedClasses = (Map<String, Class<?>>) method.invoke(null, new Object[]{});
			} catch(Exception e){
				System.err.println("获取HSF容器中的Class出现异常");
				e.printStackTrace();
			}
		}
		
		if(loadedClasses.containsKey(name)){
			return loadedClasses.get(name);
		}
		return super.loadClass(name, resolve);
	}
}
