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
package com.galaxy.diamond.common;

/**
 * 
 * @author sihai
 *
 */
public class Constants {

	public static final String CURRENT_VERSION = "0.0.1";
	
	/**
	 * 
	 */
	public static int RUN_MODE_TEST = 0;
	
	/**
	 * 
	 */
	public static int RUN_MODE_PRODUCTION = 1;
	
	/**
	 * 
	 */
	public static int DEFAULT_SERVER_PORT = 8206;
	
	/**
	 * 
	 */
	public static int DEFAULT_SERVER_MAX_THREAD_POOL_SIZE = 512;
	
	/**
	 * 
	 */
	public static int DEFAULT_SERVER_MIN_THREAD_POOL_SIZE = 1;
	
	/**
	 * 超时时间key
	 */
	public static final String TIMEOUT_KEY = "timeout";
	
	/**
	 * 默认超时时间3000ms
	 */
	public static final Integer DEFAULT_TIMEOUT = 3000;
	
	/**
	 * idle超时时间key
	 */
	public static final String IDLE_TIMEOUT_KEY = "timeout";
	
	/**
	 * 默认idle超时时间600ms
	 */
	public static final Integer DEFAULT_IDLE_TIMEOUT = 600;
	
	public static final String HESSIAN_SERIALIZE = "hessian";
	public static final String JAVA_SERIALIZE = "hessian";
	
	/**
	 * 序列化类型
	 */
	public static final String SERIALIZE_TYPE_KEY = "serialize_type";
	
	/**
	 * 默认序列化类型hessian
	 */
	public static final String DEFAULT_SERIALIZE_TYPE = HESSIAN_SERIALIZE;
	
	/**
	 * 客户端连接重试次数key
	 */
	public static final String CLIENT_RETRY_CONNECTION_TIMES_KEY = "client_retry_connection_times";
	
	/**
	 * 默认客户端连接重试次数, 3次
	 */
	public static final Integer DEFAULT_CLIENT_RETRY_CONNECTION_TIMES = 3;
	
	/**
	 * 客户端连接重试timeout key
	 */
	public static final String CLIENT_RETRY_CONNECTION_TIMEOUT_KEY = "client_retry_connection_timeout";
	
	/**
	 * 默认客户端连接重试timeout, 1000ms
	 */
	public static final Integer DEFAULT_CLIENT_RETRY_CONNECTION_TIMEOUT = 1000;
	
	
	/**
     * HSF  bunlde name
     */
    public static final String HSF_INVOKE_RELIABLE_BUNDLE_NAME = "hsf.service.tbremoting.invoke.reliable";
    
    public static final String HSF_INVOKE_ONEWAY_BUNDLE_NAME = "hsf.service.tbremoting.invoke.oneway";
    
    public static final String HSF_INVOKE_FUTURE_BUNDLE_NAME = "hsf.service.tbremoting.invoke.future";
    
    public static final String HSF_INVOKE_CALLBACK_BUNDLE_NAME = "hsf.service.tbremoting.invoke.callback";
    
    public static final String HSF_XFIRE_LIB_BUNDLE_NAME = "hsf.xfire.lib";
    
    public static final String HSF_SERVICE_RPC_XFIRE_BUNDLE_NAME = "hsf.service.rpc.xfire";
    
    public static final String HSF_SERVICE_XFIRE_INVOKE_SYNC_BUNDLE_NAME = "hsf.service.xfire.invoke.sync";
    
    public static final String HSF_XFIRE_SERVICES_BUNDLE_NAME = "hsf.xfire.services";
    
    public static final String HSF_LIB_PB_BUNDLE_NAME = "hsf.lib.pb";
    
    public static final String HSF_SERVICE_PB_BUNDLE_NAME = "hsf.service.pb";
    
    /**
     * HSF Webservice protocol
     */
    public static final String HSF_WEBSERVICE_PROTOCOL = "XFIRE";
    
    public static final String PB_PROTOCOL = "PB";
}