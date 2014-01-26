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
package com.galaxy.diamond.provider.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.galaxy.diamond.common.exception.DiamondException;
import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.diamond.provider.AbstractServiceProvider;
import com.galaxy.diamond.service.MethodInvoker;

/**
 * 
 * @author sihai
 *
 */
public class DefaultServiceProvider extends AbstractServiceProvider {
	
	/**
	 * 接口class
	 */
    private transient Class<?> interfaceClass = null;
    
    /**
     * 服务提供对象
     */
    private transient Object target;
    
    /**
     * 
     */
    private transient MethodInvoker methodInvoker;
    
    /**
     * 回调处理器，每个服务方法对应的回调方法签名有如下约定：
     *     public void ${name}_callback(Object invokeContext, Object appResponse, Throwable t);
     * 回调发生时，HSF会在这个callbackHandler对象上调用约定的回调方法。
     * 具体描述参见HSFSpringConsumerBean
     */
    private transient Object callbackHandler;
    
    /**
     * 持有invokeContext的ThreadLocal
     * 任何时候不为空，便于后续流程直接使用
     */
    private transient ThreadLocal<Serializable> invokeContext = new ThreadLocal<Serializable>();
    
	/**
	 * 
	 */
	private Map<String, Method> methodMap;
	
	/**
	 * 
	 * @param metadata
	 */
	public DefaultServiceProvider(ServiceMetadata metadata) {
		super(metadata);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		if(null == metadata) {
			throw new IllegalArgumentException("metadata must not be null");
		}
		if(null == metadata.getInterfaceName() || null == target) {
			throw new IllegalArgumentException("Please set targetInterfaceName and target");
		}
		try {
			interfaceClass = Class.forName(metadata.getInterfaceName());
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(String.format("Can not load interface:%s", metadata.getInterfaceName()), e);
		}
		
		if(!interfaceClass.isInstance(target)) {
			throw new IllegalArgumentException(String.format("Target must implements interface:%s", metadata.getInterfaceName()));
		}
		Method[] methods = interfaceClass.getDeclaredMethods();
		methodMap = new HashMap<String, Method>(methods.length);
		for(Method m : methods) {
			methodMap.put(getKey(m), m);
		}
		
		generateMethodInvoker();
	}

	@Override
	public void destroy() {
		super.destroy();
		methodMap.clear();
	}

	@Override
	public MethodInvoker getMethodInvoker() {
		return methodInvoker;
	}

	@Override
	public Object invoke(String methodName, String[] parameterTypes, Object... args) throws DiamondException {
		String key = getKey(methodName, parameterTypes);
		Method method = methodMap.get(key);
		if(null == method) {
			throw new DiamondException(String.format("Can not found method:%s, key:%s", methodName, key));
		}
		try {
			return method.invoke(target, args);
		} catch (IllegalArgumentException e) {
			throw new DiamondException(e);
		} catch (IllegalAccessException e) {
			throw new DiamondException(e);
		} catch (InvocationTargetException e) {
			throw new DiamondException(e);
		}
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}
	
	/**
     * 放置调用上下文的ThreadLocal对象。由业务在调用服务方法前，调用ThreadLocal.set()传入上下文，HSF在调用发生时取用
     * 传入的上下文对象只在ReliableCallback方式时使用
     * @return
     */
	public ThreadLocal<Serializable> getInvokeContext() {
		return invokeContext;
	}

	public void setInvokeContext(ThreadLocal<Serializable> invokeContext) {
		this.invokeContext = invokeContext;
	}
	
	public Object getCallbackHandler() {
		return callbackHandler;
	}

	public void setCallbackHandler(Object callbackHandler) {
		this.callbackHandler = callbackHandler;
	}
	
	/**
	 * 
	 */
	private void generateMethodInvoker() {
		methodInvoker = new MethodInvoker() {

			@Override
			public Object invoke(String method, String[] parameterTypes, Object[] args) throws DiamondException {
				return DefaultServiceProvider.this.invoke(method, parameterTypes, args);
			}
			
		};
	}
	
	/**
	 * 
	 * @param method
	 * @return
	 */
	private String getKey(Method method) {
		Class[] cs = method.getParameterTypes();
		String[] names = new String[cs.length + 1];
		names[0] = method.getName();
		for(int i = 0; i < cs.length; i++) {
			names[i + 1] = cs[i].getName();
		}
		return StringUtils.join(names, "-");
	}
	
	/**
	 * 
	 * @param methodName
	 * @param parameterTypes
	 */
	private String getKey(String methodName, String[] parameterTypes) {
		return String.format("%s-%s", methodName, StringUtils.join(parameterTypes, "-"));
	}
}
