/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.address.AddressReadService;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.metadata.ServiceMetadata;
import com.galaxy.hsf.rpc.RPCProtocolProvider;
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
	protected RPCProtocolProvider rpcProtocolProvider;
	
	/**
	 * 
	 */
	protected AddressReadService readService;
	
	/**
	 * 
	 * @param rpcProtocolProvider
	 * @param readService
	 */
	public AbstractServiceSubscriber(RPCProtocolProvider rpcProtocolProvider, AddressReadService readService) {
		this.rpcProtocolProvider = rpcProtocolProvider;
		this.readService = readService;
	}
	
	@Override
	public void subscribe(ServiceMetadata metadata) throws HSFException {
		// load rpc protocol if need
		for(String protocol : metadata.getImportProtocols().keySet()) {
			rpcProtocolProvider.newRPCProtocol4Client(protocol);
		}
		// subscribe address of this service
		readService.subscribe(metadata.getUniqueName());
	}

}
