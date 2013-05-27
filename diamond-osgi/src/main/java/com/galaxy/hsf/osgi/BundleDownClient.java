/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.osgi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author sihai
 *
 */
public class BundleDownClient {

	private static Log logger = LogFactory.getLog(BundleDownClient.class);
	
	public static final int DOWN_SUCCESS = 0;
	public static final int DOWN_NOTFOUND = -1;
	public static final int DOWN_FAIL = -2;
	
	/**
	 * 下载BundleFile，
	 * 用下载size为校验结果正确与否，如果与服务器端传送的Header[Bundle-Server-File-Len]相符，则为正确，
	 * 或者服务器端没提供这header，而有content-length，相符，为正确。 如果都不正确，尝试再下载一次(NOT_FOUND不会尝试)。
	 * 如果下载不成功并且覆盖掉了原文件，会删除掉所下载的文件。
	 * 
	 * @param bundleFileName
	 * @param saveDir
	 * @return 返回值 3种，见常量: DOWN_XXX
	 */
	public static int download(String bundleFileName, String saveDir) {
		// TODO
		return DOWN_FAIL;
	}
}