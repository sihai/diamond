/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import com.galaxy.hsf.address.AddressingService;
import com.galaxy.hsf.rpc.RPCProtocolProvider;

/**
 * 
 * @author sihai
 *
 */
public class DefaultServiceSubscriber extends AbstractServiceSubscriber {

	/**
	 * 
	 * @param rpcProtocolProvider
	 * @param addressingService
	 */
	public DefaultServiceSubscriber(RPCProtocolProvider rpcProtocolProvider, AddressingService addressingService) {
		super(rpcProtocolProvider, addressingService);
	}
}
