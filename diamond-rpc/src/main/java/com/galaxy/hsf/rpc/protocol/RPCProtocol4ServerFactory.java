/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.protocol;

import com.galaxy.hsf.network.HSFNetworkServer.NetworkRequestHandler;


/**
 * 
 * @author sihai
 *
 */
public interface RPCProtocol4ServerFactory {

	/**
	 * 
	 * @param handler
	 * @return
	 */
	RPCProtocol4Server newProtocol(NetworkRequestHandler handler);
}
