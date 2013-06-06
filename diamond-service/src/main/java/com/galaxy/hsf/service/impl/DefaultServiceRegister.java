/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import com.galaxy.diamond.metadata.MetadataWriteService;
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
	 * @param metadataWriteService
	 */
	public DefaultServiceRegister(RPCProtocolProvider rpcProtocolProvider, MetadataWriteService metadataWriteService) {
		super(rpcProtocolProvider, metadataWriteService);
	}
}
