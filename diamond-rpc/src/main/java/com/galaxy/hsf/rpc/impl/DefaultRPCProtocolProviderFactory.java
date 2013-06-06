/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.impl;

import com.galaxy.hsf.network.HSFNetworkServer.NetworkRequestHandler;
import com.galaxy.hsf.rpc.RPCProtocolProvider;
import com.galaxy.hsf.rpc.RPCProtocolProviderFactory;

/**
 * 
 * @author sihai
 *
 */
public class DefaultRPCProtocolProviderFactory implements RPCProtocolProviderFactory {

	@Override
	public RPCProtocolProvider newRPCProtocolProvider(NetworkRequestHandler handler) {
		DefaultRPCProtocolProvider rpcProtocolProvider = new DefaultRPCProtocolProvider(handler);
		rpcProtocolProvider.initialize();
		rpcProtocolProvider.start();
		return rpcProtocolProvider;
	}

}
