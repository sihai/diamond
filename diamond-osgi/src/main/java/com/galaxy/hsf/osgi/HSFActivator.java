/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.osgi;

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

import com.galaxy.hsf.common.HSFConstants;
import com.galaxy.hsf.common.exception.HSFException;

/**
 * 
 * @author sihai
 *
 */
public class HSFActivator implements BundleActivator {

	private static final Log logger = LogFactory.getLog(HSFActivator.class);
	
	private static BundleContext bundleContext;
	
	public void start(BundleContext context) throws Exception {
		HSFActivator.bundleContext = context;
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
	
	private static void installAndStartBundle(String location) throws HSFException{
		if(bundleContext != null){
			try {
				Bundle bundle = bundleContext.installBundle(location);
				bundle.start();
			} catch (BundleException e) {
				throw new HSFException(e);
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
		return HSFConstants.CURRENT_VERSION;
	}
	
	public static  void downloadAndInstallBundle(String bundleName) throws HSFException {
    	String version = getCurrentBundleVersion();
    	String bundle = bundleName + "-" + version + ".jar.plugin";
		String containerPath = System.getProperty("HSF.CONTAINER.PATH");
		String outputDir = containerPath + File.separator + "plugins" + File.separator + "extra";
		try {
			HSFBundleDownloader.download(bundle, outputDir);
		} catch (HSFException e) {
			throw new HSFException(String.format("下载bundle:%s, 出错，出错原因为:", bundle), e);
		}
		try {
			File f = new File(outputDir, bundle);
			installAndStartBundle(f.toURI().toURL().toExternalForm());
		} catch (HSFException e) {
			throw new HSFException("安装bundle过程中出错, 出错原因为：", e);
		} catch (MalformedURLException e) {
			throw new HSFException("安装bundle过程中出错, 出错原因为：", e);
		}
	}
}