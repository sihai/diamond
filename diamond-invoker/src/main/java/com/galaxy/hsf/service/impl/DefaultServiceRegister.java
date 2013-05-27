/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import com.galaxy.hsf.address.AddressWriteService;
import com.galaxy.hsf.rpc.RPCProvider;

/**
 * 
 * @author sihai
 *
 */
public class DefaultServiceRegister extends AbstractServiceRegister {

	/**
	 * 
	 * @param rpcProvider
	 * @param writeService
	 */
	public DefaultServiceRegister(RPCProvider rpcProvider, AddressWriteService writeService) {
		super(rpcProvider, writeService);
	}
}
