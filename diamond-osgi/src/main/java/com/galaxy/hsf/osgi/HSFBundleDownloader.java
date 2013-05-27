/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.osgi;

import com.galaxy.hsf.common.exception.HSFException;

/**
 * 
 * @author sihai
 *
 */
public class HSFBundleDownloader {
	
	public static void download(String bundleName , String outputDir) throws HSFException{
		int ret = BundleDownClient.download(bundleName, outputDir);
		if(BundleDownClient.DOWN_SUCCESS != ret){
			throw new HSFException(String.format("下载Bundle文件:%s失败,错误代码为:%d", bundleName, ret));
		}
	}
	
	public static void main(String[] args) {
		try {
			long start = System.currentTimeMillis();
			HSFBundleDownloader.download("hsf.xfire.lib-1.6.0.jar.plugin", "D:/download");
			long end = System.currentTimeMillis();
			System.out.println("用时："+(end-start)+"ms");
		} catch (HSFException e) {
			e.printStackTrace();
		}
	}
}