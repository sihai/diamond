/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.impl;

import com.galaxy.hsf.common.HSFRequest;
import com.galaxy.hsf.common.HSFResponse;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.network.HSFNetworkServer;
import com.galaxy.hsf.rpc.protocol.RPCProtocol4Client;

/**
 * 
 * @author sihai
 *
 */
public class DefaultRPCInvoker extends AbstractRPCInvoker {
	
	/**
	 * 
	 * @param handler
	 */
	public DefaultRPCInvoker(HSFNetworkServer.NetworkRequestHandler handler) {
		super(handler);
	}
	
	@Override
	public HSFResponse invoke(HSFRequest request) throws HSFException {
		RPCProtocol4Client protocol = rpcProvider.newRPCProtocol4Client(request.getServiceURL());
		return protocol.invoke(request);
	}

}
