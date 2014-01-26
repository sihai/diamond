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
 * 
 */
package com.openteach.diamond.service.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.address.Protocol;
import com.openteach.diamond.common.Request;
import com.openteach.diamond.common.exception.DiamondException;
import com.openteach.diamond.common.lifecycle.AbstractLifeCycle;
import com.openteach.diamond.metadata.ServiceURL;
import com.openteach.diamond.router.RouteParameter;
import com.openteach.diamond.router.ServiceRouter;
import com.openteach.diamond.rpc.RPCProtocolProvider;
import com.openteach.diamond.service.MethodInvoker;
import com.openteach.diamond.service.ServiceInvoker;
import com.openteach.diamond.util.NetworkUtil;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractServiceInvoker extends AbstractLifeCycle implements ServiceInvoker {

	private static final Log logger = LogFactory.getLog(AbstractServiceInvoker.class);
	
	/**
	 * 
	 */
	protected ServiceRouter serviceRouter;
	
	/**
	 * 
	 */
	protected RPCProtocolProvider rpcProtocolProvider;
	
	private ConcurrentHashMap<String, MethodInvoker> methodInvokerMap;
	

	/**
	 * 
	 * @param serviceRouter
	 * @param rpcProvider
	 */
	public AbstractServiceInvoker(ServiceRouter serviceRouter, RPCProtocolProvider rpcProtocolProvider) {
		this.serviceRouter = serviceRouter;
		this.rpcProtocolProvider = rpcProtocolProvider;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		methodInvokerMap = new ConcurrentHashMap<String, MethodInvoker>();
	}


	@Override
	public void destroy() {
		super.destroy();
		methodInvokerMap.clear();
	}
	
	@Override
	public void register(String serviceName, MethodInvoker invoker) {
		methodInvokerMap.put(serviceName, invoker);
	}

	@Override
	public void unregister(String serviceName) {
		methodInvokerMap.remove(serviceName);
	}

	@Override
	public Object invokeLocal(String serviceName, String method, String[] parameterTypes, Object[] args) throws DiamondException {
		MethodInvoker invoker = methodInvokerMap.get(serviceName);
		if(null == invoker) {
			throw new DiamondException(String.format(String.format("OOPS: can not found service for name:%s", serviceName)));
		}
		return invoker.invoke(method, parameterTypes, args);
	}

	@Override
	public Object invokeRemote(String serviceName, String method, String[] parameterTypes, Object[] args, String protocol) throws DiamondException {
		RouteParameter parameter = new RouteParameter();
		parameter.setServiceName(serviceName);
		parameter.setMethod(method);
		parameter.setParameterTypes(parameterTypes);
		parameter.setArgs(args);
		parameter.setProtocol(Protocol.toEnum(protocol));
		List<ServiceURL> addresses = serviceRouter.route(parameter);
		if(addresses.isEmpty()) {
			throw new DiamondException(String.format("Not route for servieName:%s, protocol:%s", serviceName, protocol));
		}
		return invoke0(constructRequest(serviceName, method, parameterTypes, args, protocol), addresses);
	}
	
	/**
	 * 
	 * @param request
	 * @param addresses
	 * @return
	 * @throws DiamondException
	 */
	protected abstract Object invoke0(Request request, List<ServiceURL> addresses) throws DiamondException;

	/**
	 * 
	 * @param serviceName
	 * @param method
	 * @param parameterTypes
	 * @param args
	 * @param protocol
	 * @return
	 */
	protected Request constructRequest(String serviceName, String method, String[] parameterTypes, Object[] args, String protocol) throws DiamondException {
		Request request = new Request();
		request.setServiceName(serviceName);
		request.setMethod(method);
		request.setParameterTypes(parameterTypes);
		request.setLocalAddress(NetworkUtil.getLocalIp());
		request.setArgs(args);
		request.setProtocol(protocol);
		return request;
	}
}
