/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import com.galaxy.hsf.address.AddressWriteService;
import com.galaxy.hsf.rpc.RPCProtocolProvider;

/**
 * 
 * @author sihai
 *
 */
public class DefaultServiceRegister extends AbstractServiceRegister {

	/**
	 * 
	 * @param rpcProtocolProvider
	 * @param writeService
	 */
	public DefaultServiceRegister(RPCProtocolProvider rpcProtocolProvider, AddressWriteService writeService) {
		super(rpcProtocolProvider, writeService);
	}
}
