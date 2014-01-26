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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.osgi.baseadaptor.HookConfigurator;
import org.eclipse.osgi.baseadaptor.HookRegistry;
import org.eclipse.osgi.framework.adaptor.BundleClassLoader;
import org.eclipse.osgi.framework.adaptor.BundleData;
import org.eclipse.osgi.framework.adaptor.ClassLoaderDelegateHook;

/**
 * 
 * 描述：以便和外部容器集成，加载类和资源时可以从外部容器加载<br>
 * 类加载先在OSGi中加载，然后到外部容器中加载<br>
 * 资源加载先到外部容器加载，然后在OSGi中加载
 * 
 * @author sihai
 *
 */
public class DefaultClassLoaderDelegateHook implements ClassLoaderDelegateHook, HookConfigurator {

	private static List<ClassLoader> thirdClassLoaders = new ArrayList<ClassLoader>();
	
	private static List<String> excludedPrefixes = null;
	
	static {
		String excluded = System.getProperty("com.galaxy.diamond.container.excluded.prefixes");
		if (excluded != null) {
			excludedPrefixes = Arrays.asList(excluded.split(","));
		} else {
			excludedPrefixes = new ArrayList<String>();
			//默认只有spring,log4j,common-logging优先从容器外部加载，其他都先在OSGi中加载，然后到外部容器中加载
			excludedPrefixes.add("org.springframework");
			excludedPrefixes.add("com.alibaba.common.lang");
			//excludedPrefixes.add("org.apache.log4j");
			//excludedPrefixes.add("org.apache.commons.logging");
		}
	}
	
	public static void addThirdClassLoader(ClassLoader loader){
		thirdClassLoaders.add(loader);
	}
	
	public Class preFindClass(String className, BundleClassLoader classloader, BundleData data) throws ClassNotFoundException {
		boolean loadFromExternal = false;
		for (String prefix : excludedPrefixes) {
			if (className.startsWith(prefix)) {
				loadFromExternal = true;
				break;
			}
		}
		if (loadFromExternal) {
			for (ClassLoader thirdClassLoader : thirdClassLoaders) {
				if (thirdClassLoader != null) {
					try {
						return thirdClassLoader.loadClass(className);
					} catch (ClassNotFoundException e) {
						// IGNORE,continue find
					}
				}
			}
		}
		return null;
	}
	
	public Class postFindClass(String name, BundleClassLoader classloader, BundleData data) throws ClassNotFoundException {
		// 避免应用中groovy包的类造成影响
		if((name != null) && (name.startsWith("groovy.runtime.metaclass"))){
			return null;
		}
		for (ClassLoader thirdClassLoader : thirdClassLoaders) {
			if(thirdClassLoader!=null){
				try{
					return thirdClassLoader.loadClass(name);
				}
				catch(ClassNotFoundException e){
					// IGNORE,continue find
				}
			}
		}
		return null;
	}
	
	public URL preFindResource(String name, BundleClassLoader classloader, BundleData data) throws FileNotFoundException {
		for (ClassLoader thirdClassLoader : thirdClassLoaders) {
			if(thirdClassLoader!=null){
				try{
					return thirdClassLoader.getResource(name);
				}
				catch(Exception e){
					// IGNORE
				}
			}
		}
		return null;
	}
	
	public URL postFindResource(String name, BundleClassLoader classloader, BundleData data) throws FileNotFoundException {
		return null;
	}
	
	public Enumeration preFindResources(String name, BundleClassLoader classloader, BundleData data) throws FileNotFoundException {
		for (ClassLoader thirdClassLoader : thirdClassLoaders) {
			if(thirdClassLoader!=null)
				try{
					return thirdClassLoader.getResources(name);
				}
				catch (IOException e) {
					// IGNORE
				}
		}
		return null;
	}
	
	public Enumeration postFindResources(String name, BundleClassLoader classloader, BundleData data) throws FileNotFoundException {
		return null;
	}
	
	public String preFindLibrary(String name, BundleClassLoader classloader, BundleData data) throws FileNotFoundException {
		return null;
	}
	
	public String postFindLibrary(String name, BundleClassLoader classloader, BundleData data) {
		return null;
	}
	
	public void addHooks(HookRegistry registry) {
		registry.addClassLoaderDelegateHook(this);
	}
}
