/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import com.galaxy.hsf.address.AddressReadService;
import com.galaxy.hsf.rpc.RPCProvider;

/**
 * 
 * @author sihai
 *
 */
public class DefaultServiceSubscriber extends AbstractServiceSubscriber {

	/**
	 * 
	 * @param rpcProvider
	 * @param readService
	 */
	public DefaultServiceSubscriber(RPCProvider rpcProvider, AddressReadService readService) {
		super(rpcProvider, readService);
	}
}
