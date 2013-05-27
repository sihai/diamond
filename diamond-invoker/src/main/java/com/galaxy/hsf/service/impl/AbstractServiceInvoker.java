/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.address.Protocol;
import com.galaxy.hsf.common.HSFRequest;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.router.RouteParameter;
import com.galaxy.hsf.router.ServiceRouter;
import com.galaxy.hsf.rpc.RPCProvider;
import com.galaxy.hsf.service.ServiceInvoker;
import com.galaxy.hsf.util.NetworkUtil;

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
	protected RPCProvider rpcProvider;
	
	/**
	 * 
	 * @param serviceRouter
	 * @param rpcProvider
	 */
	public AbstractServiceInvoker(ServiceRouter serviceRouter, RPCProvider rpcProvider) {
		this.serviceRouter = serviceRouter;
		this.rpcProvider = rpcProvider;
	}
	
	
	@Override
	public Object invoke(String serviceName, String method, String[] parameterTypes, Object[] args, String protocol) throws HSFException {
		RouteParameter parameter = new RouteParameter();
		parameter.setServiceName(serviceName);
		parameter.setMethod(method);
		parameter.setParameterTypes(parameterTypes);
		parameter.setArgs(args);
		parameter.setProtocol(Protocol.toEnum(protocol));
		List<String> addresses = serviceRouter.route(parameter);
		if(addresses.isEmpty()) {
			throw new HSFException(String.format("Not route for servieName:%s, protocol:%s", serviceName, protocol));
		}
		return invoke0(constructRequest(serviceName, method, parameterTypes, args, protocol), addresses);
	}
	
	/**
	 * 
	 * @param request
	 * @param addresses
	 * @return
	 * @throws HSFException
	 */
	protected abstract Object invoke0(HSFRequest request, List<String> addresses) throws HSFException;

	/**
	 * 
	 * @param serviceName
	 * @param method
	 * @param parameterTypes
	 * @param args
	 * @param protocol
	 * @return
	 */
	protected HSFRequest constructRequest(String serviceName, String method, String[] parameterTypes, Object[] args, String protocol) throws HSFException {
		try {
			HSFRequest request = new HSFRequest();
			request.setServiceName(serviceName);
			request.setMethod(method);
			request.setParameterTypes(parameterTypes);
			request.setLocalAddress(NetworkUtil.getLocalIp());
			request.setArgs(args);
			return request;
		} catch (UnknownHostException e) {
			throw new HSFException("Can not got ip of local host", e);
		}
	}
}
