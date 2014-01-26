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