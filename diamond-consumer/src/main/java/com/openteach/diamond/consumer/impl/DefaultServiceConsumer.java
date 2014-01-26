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
package com.openteach.diamond.consumer.impl;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.openteach.diamond.common.exception.DiamondException;
import com.openteach.diamond.consumer.AbstractServiceConsumer;
import com.openteach.diamond.metadata.ServiceMetadata;
import com.openteach.diamond.rpc.protocol.diamond.DefaultRPCProtocol4Client;
import com.openteach.diamond.service.ServiceInvoker;
import com.openteach.diamond.service.ServiceSubscriber;

/**
 * 
 * @author sihai
 *
 */
public class DefaultServiceConsumer extends AbstractServiceConsumer {

	/**
	 * 
	 */
	private Class<?> interfaceClass;
	
	/**
	 * 
	 */
	private ServiceSubscriber subscriber;
	
	/**
	 * 
	 */
	private ServiceInvoker serviceInvoker;
	
	/**
	 * 
	 */
	private Object proxy;
	
	/**
	 * 
	 */
	private Map<String, Method> methodMap;
	
	/**
	 * 
	 * @param metadata
	 * @param subscriber
	 * @param serviceInvoker
	 */
	public DefaultServiceConsumer(ServiceMetadata metadata, ServiceSubscriber subscriber, ServiceInvoker serviceInvoker) {
		super(metadata);
		this.subscriber = subscriber;
		this.serviceInvoker = serviceInvoker;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		if(null == metadata) {
			throw new IllegalArgumentException("metadata must not be null");
		}
		if(null == metadata.getInterfaceName()) {
			throw new IllegalArgumentException("Please set targetInterfaceName and target");
		}
		try {
			interfaceClass = Class.forName(metadata.getInterfaceName());
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(String.format("Can not load interface:%s", metadata.getInterfaceName()), e);
		}
		// subscribe service
		try {
			subscriber.subscribe(metadata);
		} catch (DiamondException e) {
			throw new RuntimeException(String.format("Subscribe service:%s failed", metadata.getUniqueName()), e);
		}
		generateProxy();
	}

	@Override
	public void destroy() {
		super.destroy();
		methodMap.clear();
	}

	@Override
	public Object getProxy() {
		return proxy;
	}

	@Override
	public Object invoke(String methodName, String[] parameterTypes, Object[] args, String protocol) throws DiamondException {
		return serviceInvoker.invokeRemote(metadata.getUniqueName(), methodName, parameterTypes, args, protocol);
	}
	
	/**
	 * 
	 * @param method
	 * @return
	 */
	private String[] getParameterTypes(Method method) {
		Class<?>[] clazzs = method.getParameterTypes();
		String[] types = new String[clazzs.length];
		for(int i = 0; i < clazzs.length; i++) {
			types[i] = clazzs[i].getName();
		}
		return types;
	}
	
	private static final AtomicInteger NO = new AtomicInteger(0);
	/**
	 * 
	 */
	private static NamingPolicy POLICY = new NamingPolicy() {

		@Override
		public String getClassName(String prefix, String source, Object key, Predicate names) {
			return String.format("%s-Proxy-%d", DefaultServiceConsumer.class.getSimpleName(), NO.getAndIncrement());
		}
		
	};
	
	/**
	 * 
	 */
	private void generateProxy() {
		// TODO
		Enhancer enhancer = new Enhancer();
		enhancer.setNamingPolicy(POLICY);
		enhancer.setInterfaces(new Class[]{interfaceClass});
		enhancer.setCallback(new MethodInterceptor() {

			@Override
			public Object intercept(Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				return invoke(method.getName(), getParameterTypes(method), args, DefaultRPCProtocol4Client.PROTOCOL);
			}
			 
		});
		proxy = enhancer.create();
	}
}
