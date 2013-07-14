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
package com.galaxy.diamond.rpc.impl;

import java.lang.reflect.Method;

import com.galaxy.diamond.common.Request;
import com.galaxy.diamond.common.Response;
import com.galaxy.diamond.common.exception.DiamondException;
import com.galaxy.diamond.common.lifecycle.AbstractLifeCycle;
import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.diamond.network.HSFNetworkServer;
import com.galaxy.diamond.rpc.RPCInvoker;
import com.galaxy.diamond.rpc.RPCProtocolProvider;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractRPCInvoker extends AbstractLifeCycle implements RPCInvoker {
	
	/**
	 * 
	 */
	private HSFNetworkServer.NetworkRequestHandler handler;
	
	/**
	 * 
	 */
	protected RPCProtocolProvider rpcProvider;
	
	/**
	 * 
	 * @param handler
	 */
	public AbstractRPCInvoker(HSFNetworkServer.NetworkRequestHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		rpcProvider = new DefaultRPCProtocolProvider(handler);
		rpcProvider.initialize();
		rpcProvider.start();
	}

	@Override
	public void destroy() {
		super.destroy();
		rpcProvider.destroy();
	}

	/**
	 * 
	 * @param request
	 * @throws DiamondException
	 */
	protected void before(Request request) throws DiamondException {
		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws DiamondException
	 */
	protected void after(Request request, Response response) throws DiamondException {
		
	}
	
	
	@Override
	public Response invoke(String serviceURL, ServiceMetadata metadata, Method method, Object... args) throws DiamondException {
		Request request = constructRequest(serviceURL, metadata, method, args);
		before(request);
		Response response = invoke(request);
		after(request, response);
		return response;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws DiamondException
	 */
	protected abstract Response invoke(Request request) throws DiamondException;

	/**
	 * 
	 * @param serviceURL
	 * @param metadata
	 * @param method
	 * @param args
	 * @return
	 */
	protected Request constructRequest(String serviceURL, ServiceMetadata metadata, Method method, Object... args) {
		// TODO
		return null;
	}
}
