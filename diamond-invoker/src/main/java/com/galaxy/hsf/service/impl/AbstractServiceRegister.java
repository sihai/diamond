/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.hsf.address.AddressWriteService;
import com.galaxy.hsf.address.Protocol;
import com.galaxy.hsf.address.ServiceAddress;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.rpc.RPCProvider;
import com.galaxy.hsf.service.ServiceRegister;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractServiceRegister extends AbstractLifeCycle implements ServiceRegister {
	
	private static final Log logger = LogFactory.getLog(AbstractServiceRegister.class);
	
	/**
	 * 
	 */
	protected RPCProvider rpcProvider;
	
	/**
	 * 
	 */
	protected AddressWriteService writeService;
	
	/**
	 * 
	 * @param rpcProvider
	 * @param writeService
	 */
	public AbstractServiceRegister(RPCProvider rpcProvider, AddressWriteService writeService) {
		this.rpcProvider = rpcProvider;
		this.writeService = writeService;
	}
	
	@Override
	public void register(ServiceMetadata metadata) {
		writeService.publish(metadata2Adress(metadata));
	}

	@Override
	public void unregister(ServiceMetadata metadata) {
		writeService.remove(metadata2Adress(metadata));
	}
	
	/**
	 * 
	 * @param metadata
	 * @return
	 */
	private ServiceAddress metadata2Adress(ServiceMetadata metadata) {
		ServiceAddress sa = new ServiceAddress(metadata.getUniqueName());
		for(Map.Entry<String, Properties> e : metadata.getExportProtocols().entrySet()) {
			sa.addAddress(Protocol.toEnum(e.getKey()), rpcProvider.newRPCProtocol(e.getKey()).constructURL(metadata.getUniqueName(), e.getValue()).toString());
		}
		return sa;
	}

}
