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
package com.openteach.diamond.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * HSF Request
 * @author sihai
 *
 */
public class Request implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5580333931494605468L;

	private String protocol;
	
	/**
	 * 
	 */
	private String serviceURL;

	/**
	 * 
	 */
	private String serviceName;
	
	/**
	 * 
	 */
	private String method;

	/**
	 * 
	 */
    private String[] parameterTypes;

    // 由于要处理父类、子类问题，因此将参数对象这个地方改为另外的方式来序列化
    private Object[] args;

    /**
     * 
     */
    private String localAddress;
    
    /**
     * 存放调用上下文序列化之后的结果
     * 直接存放bytes，服务端就不需要因为反序列化而包含客户端的Context类了
     */
    private byte[] invokeContext;
    
    private boolean isNeedReliableCallback;
    
    private Map<String, Object> properties = new HashMap<String, Object>();

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(String[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	public byte[] getInvokeContext() {
		return invokeContext;
	}

	public void setInvokeContext(byte[] invokeContext) {
		this.invokeContext = invokeContext;
	}

	public boolean isNeedReliableCallback() {
		return isNeedReliableCallback;
	}

	public void setNeedReliableCallback(boolean isNeedReliableCallback) {
		this.isNeedReliableCallback = isNeedReliableCallback;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}