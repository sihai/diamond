/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc;

import com.galaxy.hsf.network.HSFNetworkServer;

/**
 * 
 * @author sihai
 *
 */
public interface RPCProtocolProviderFactory {

	/**
	 * 
	 * @param handler
	 * @return
	 */
	RPCProtocolProvider newRPCProtocolProvider(HSFNetworkServer.NetworkRequestHandler handler);
}
