/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.diamond.metadata.MetadataWriteService;
import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.hsf.address.Protocol;
import com.galaxy.hsf.address.ServiceAddress;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.rpc.RPCProtocolProvider;
import com.galaxy.hsf.service.MethodInvoker;
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
	protected RPCProtocolProvider rpcProtocolProvider;
	
	/**
	 * 
	 */
	protected MetadataWriteService metadataWriteService;
	
	/**
	 * 
	 * @param rpcProvider
	 * @param metadataWriteService
	 */
	public AbstractServiceRegister(RPCProtocolProvider rpcProtocolProvider, MetadataWriteService metadataWriteService) {
		this.rpcProtocolProvider = rpcProtocolProvider;
		this.metadataWriteService = metadataWriteService;
	}
	
	@Override
	public void register(ServiceMetadata metadata, MethodInvoker invoker) throws HSFException {
		for(Map.Entry<String, Properties> e : metadata.getExportProtocols().entrySet()) {
			metadata.addAddress(e.getKey(), rpcProtocolProvider.newRPCProtocol4Server(e.getKey()).constructURL(metadata.getUniqueName(), e.getValue()).toString());
		}
		this.metadataWriteService.register(metadata);
	}

	@Override
	public void unregister(ServiceMetadata metadata) throws HSFException {
		metadataWriteService.unregister(metadata);
	}
	
	/**
	 * 
	 * @param metadata
	 * @return
	 * @throws HSFException
	 */
	private ServiceAddress metadata2Adress(ServiceMetadata metadata) throws HSFException {
		ServiceAddress sa = new ServiceAddress(metadata.getUniqueName());
		for(Map.Entry<String, Properties> e : metadata.getExportProtocols().entrySet()) {
			sa.addAddress(Protocol.toEnum(e.getKey()), rpcProtocolProvider.newRPCProtocol4Server(e.getKey()).constructURL(metadata.getUniqueName(), e.getValue()).toString());
		}
		return sa;
	}

}
