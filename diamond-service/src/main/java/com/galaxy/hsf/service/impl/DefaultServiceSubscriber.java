/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import com.galaxy.hsf.address.AddressReadService;
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
	 * @param readService
	 */
	public DefaultServiceSubscriber(RPCProtocolProvider rpcProtocolProvider, AddressReadService readService) {
		super(rpcProtocolProvider, readService);
	}
}
