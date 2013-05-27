/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.address.AddressWriteService;
import com.galaxy.hsf.address.Protocol;
import com.galaxy.hsf.address.ServiceAddress;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.metadata.ServiceMetadata;
import com.galaxy.hsf.rpc.RPCProtocolProvider;
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
	protected AddressWriteService writeService;
	
	/**
	 * 
	 * @param rpcProvider
	 * @param writeService
	 */
	public AbstractServiceRegister(RPCProtocolProvider rpcProtocolProvider, AddressWriteService writeService) {
		this.rpcProtocolProvider = rpcProtocolProvider;
		this.writeService = writeService;
	}
	
	@Override
	public void register(ServiceMetadata metadata) throws HSFException {
		writeService.publish(metadata2Adress(metadata));
	}

	@Override
	public void unregister(ServiceMetadata metadata) throws HSFException {
		writeService.remove(metadata2Adress(metadata));
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
