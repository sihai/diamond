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
 */
package com.openteach.diamond.metadata;

import java.io.Serializable;

/**
 * 
 * @author sihai
 *
 */
public class MethodSpecial implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7408477143098483199L;
	
	public static final String left = "[";
	public static final String right = "]";
	public static final String equal = "=";
	public static final String split = "#";
	
	private static final String KEY_TIMEOUT = "clientTimeout";
    
	/**
	 * 方法名称
	 */
	private String methodName;
	
	/**
     * 默认客户端调用超时时间：3000ms
     */
    private long clientTimeout = 3000;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public long getClientTimeout() {
		return clientTimeout;
	}

	public void setClientTimeout(long clientTimeout) {
		this.clientTimeout = clientTimeout;
	}
	
	public static MethodSpecial parseMethodSpecial(String str) {
        int idx_split = str.indexOf(split); // "#"被认为是MethodSpecial配置的标志
        if (idx_split < 0) {
            return null;
        }

        int idx_equal = str.indexOf(equal);
        int idx_leftLeft = str.indexOf(left);
        int idx_leftRight = str.indexOf(right);
        int idx_rightLeft = str.indexOf(left, idx_equal);
        int idx_rightRight = str.indexOf(right, idx_equal);

        String methodName = str.substring(idx_leftLeft + 1, idx_leftRight);
        String key = str.substring(idx_rightLeft + 1, idx_split);
        String value = str.substring(idx_split + 1, idx_rightRight);

        MethodSpecial special = new MethodSpecial();
        special.setMethodName(methodName);
        if (KEY_TIMEOUT.equals(key)) {
            special.setClientTimeout(Long.parseLong(value));
        }
        return special;
    }
}