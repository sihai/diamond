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
 * 
 */
package com.openteach.diamond.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.common.exception.DiamondException;
import com.openteach.diamond.common.lifecycle.AbstractLifeCycle;
import com.openteach.diamond.metadata.ServiceMetadata;
import com.openteach.diamond.service.DiamondService;
import com.openteach.diamond.service.MethodInvoker;
import com.openteach.diamond.service.ServiceInvoker;
import com.openteach.diamond.service.ServiceRegister;
import com.openteach.diamond.service.ServiceSubscriber;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractDiamondService extends AbstractLifeCycle implements DiamondService {

	private static final Log logger = LogFactory.getLog(AbstractDiamondService.class);
	
	/**
	 * 
	 */
	protected ServiceRegister serviceRegister;
	
	/**
	 * 
	 */
	protected ServiceSubscriber serviceSubscriber;
	
	/**
	 * 
	 */
	protected ServiceInvoker serviceInvoker;
	
	/**
	 * 
	 * @param serviceRegister
	 * @param serviceSubscriber
	 * @param serviceInvoker
	 */
	public AbstractDiamondService(ServiceRegister serviceRegister, ServiceSubscriber serviceSubscriber, ServiceInvoker serviceInvoker) {
		this.serviceRegister = serviceRegister;
		this.serviceSubscriber = serviceSubscriber;
		this.serviceInvoker = serviceInvoker;
	}
	
	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public void register(ServiceMetadata metadata, MethodInvoker invoker) throws DiamondException {
		serviceRegister.register(metadata, invoker);
		register(metadata.getUniqueName(), invoker);
	}

	@Override
	public void unregister(ServiceMetadata metadata) throws DiamondException {
		serviceRegister.unregister(metadata);
		unregister(metadata.getUniqueName());
	}

	@Override
	public void subscribe(ServiceMetadata metadata) throws DiamondException {
		serviceSubscriber.subscribe(metadata);
	}

	@Override
	public void register(String serviceName, MethodInvoker invoker) {
		serviceInvoker.register(serviceName, invoker);
	}

	@Override
	public void unregister(String serviceName) {
		serviceInvoker.unregister(serviceName);
	}
	@Override
	public Object invokeLocal(String serviceName, String method, String[] parameterTypes, Object[] args) throws DiamondException {
		return serviceInvoker.invokeLocal(serviceName, method, parameterTypes, args);
	}
	
	@Override
	public Object invokeRemote(String serviceName, String method, String[] parameterTypes, Object[] args, String protocol) throws DiamondException {
		return serviceInvoker.invokeRemote(serviceName, method, parameterTypes, args, protocol);
	}
	
}
