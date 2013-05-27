/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.impl;

import java.lang.reflect.Method;

import com.galaxy.hsf.common.HSFRequest;
import com.galaxy.hsf.common.HSFResponse;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.metadata.ServiceMetadata;
import com.galaxy.hsf.network.HSFNetworkServer;
import com.galaxy.hsf.rpc.RPCInvoker;
import com.galaxy.hsf.rpc.RPCProtocolProvider;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractRPCInvoker extends AbstractLifeCycle implements RPCInvoker {
	
	/**
	 * 
	 */
	private HSFNetworkServer.HSFRequestHandler handler;
	
	/**
	 * 
	 */
	protected RPCProtocolProvider rpcProvider;
	
	/**
	 * 
	 * @param handler
	 */
	public AbstractRPCInvoker(HSFNetworkServer.HSFRequestHandler handler) {
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
	 * @throws HSFException
	 */
	protected void before(HSFRequest request) throws HSFException {
		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws HSFException
	 */
	protected void after(HSFRequest request, HSFResponse response) throws HSFException {
		
	}
	
	
	@Override
	public HSFResponse invoke(String serviceURL, ServiceMetadata metadata, Method method, Object... args) throws HSFException {
		HSFRequest request = constructRequest(serviceURL, metadata, method, args);
		before(request);
		HSFResponse response = invoke(request);
		after(request, response);
		return response;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws HSFException
	 */
	protected abstract HSFResponse invoke(HSFRequest request) throws HSFException;

	/**
	 * 
	 * @param serviceURL
	 * @param metadata
	 * @param method
	 * @param args
	 * @return
	 */
	protected HSFRequest constructRequest(String serviceURL, ServiceMetadata metadata, Method method, Object... args) {
		// TODO
		return null;
	}
}
