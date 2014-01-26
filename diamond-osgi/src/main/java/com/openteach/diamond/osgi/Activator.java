/**
 * Copyright 2013 openteach
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
 */
package com.openteach.diamond.osgi;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.osgi.framework.internal.core.AbstractBundle;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;

import com.openteach.diamond.common.Constants;
import com.openteach.diamond.common.exception.DiamondException;

/**
 * 
 * @author sihai
 *
 */
public class Activator implements BundleActivator {

	private static final Log logger = LogFactory.getLog(Activator.class);
	
	private static BundleContext bundleContext;
	
	public void start(BundleContext context) throws Exception {
		Activator.bundleContext = context;
	}

	public void stop(BundleContext context) throws Exception {
		if (bundleContext == context) {
			bundleContext = null;
		}
	}
	
	public static boolean isBundleExist(String symbolicName){
		if(bundleContext != null){
			Bundle[] bundles = bundleContext.getBundles();
			if(bundles != null){
				for(Bundle bundle : bundles){
					if(symbolicName.equals(bundle.getSymbolicName())){
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}
	
	private static void installAndStartBundle(String location) throws DiamondException{
		if(bundleContext != null){
			try {
				Bundle bundle = bundleContext.installBundle(location);
				bundle.start();
			} catch (BundleException e) {
				throw new DiamondException(e);
			}
		}
	}
	
	/**
	 * 
	 * @return current bundle version
	 */
	private static String getCurrentBundleVersion(){
		Bundle bundle = bundleContext.getBundle();
		if(bundle instanceof AbstractBundle){
			AbstractBundle aBundle = (AbstractBundle)bundle;
			Version version =  aBundle.getVersion();
			if(version != null){
				return version.toString();
			}
		}
		return Constants.CURRENT_VERSION;
	}
	
	public static  void downloadAndInstallBundle(String bundleName) throws DiamondException {
    	String version = getCurrentBundleVersion();
    	String bundle = bundleName + "-" + version + ".jar.plugin";
		String containerPath = System.getProperty("HSF.CONTAINER.PATH");
		String outputDir = containerPath + File.separator + "plugins" + File.separator + "extra";
		try {
			BundleDownloader.download(bundle, outputDir);
		} catch (DiamondException e) {
			throw new DiamondException(String.format("下载bundle:%s, 出错，出错原因为:", bundle), e);
		}
		try {
			File f = new File(outputDir, bundle);
			installAndStartBundle(f.toURI().toURL().toExternalForm());
		} catch (DiamondException e) {
			throw new DiamondException("安装bundle过程中出错, 出错原因为：", e);
		} catch (MalformedURLException e) {
			throw new DiamondException("安装bundle过程中出错, 出错原因为：", e);
		}
	}
}