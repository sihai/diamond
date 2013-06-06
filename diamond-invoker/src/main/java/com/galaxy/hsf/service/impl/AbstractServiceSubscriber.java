/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.hsf.address.AddressingService;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.rpc.RPCProvider;
import com.galaxy.hsf.service.ServiceSubscriber;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractServiceSubscriber extends AbstractLifeCycle implements ServiceSubscriber {

	private static final Log logger = LogFactory.getLog(AbstractServiceSubscriber.class);
	
	/**
	 * 
	 */
	protected RPCProvider rpcProvider;
	
	/**
	 * 
	 */
	protected AddressingService readService;
	
	/**
	 * 
	 * @param rpcProvider
	 * @param readService
	 */
	public AbstractServiceSubscriber(RPCProvider rpcProvider, AddressingService readService) {
		this.rpcProvider = rpcProvider;
		this.readService = readService;
	}
	
	@Override
	public void subscribe(ServiceMetadata metadata) {
		// load rpc protocol if need
		for(String protocol : metadata.getImportProtocols().keySet()) {
			rpcProvider.newRPCProtocol(protocol);
		}
		// subscribe address of this service
		readService.addressing(metadata.getUniqueName());
	}

}
