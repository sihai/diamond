/**
 * Copyright 2013 Qiangqiang RAO
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 */
package com.galaxy.diamond.service.impl;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.diamond.address.Protocol;
import com.galaxy.diamond.address.ServiceAddress;
import com.galaxy.diamond.common.exception.DiamondException;
import com.galaxy.diamond.common.lifecycle.AbstractLifeCycle;
import com.galaxy.diamond.metadata.MetadataWriteService;
import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.diamond.rpc.RPCProtocolProvider;
import com.galaxy.diamond.service.MethodInvoker;
import com.galaxy.diamond.service.ServiceRegister;

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
	public void register(ServiceMetadata metadata, MethodInvoker invoker) throws DiamondException {
		for(Map.Entry<String, Properties> e : metadata.getExportProtocols().entrySet()) {
			metadata.addAddress(e.getKey(), rpcProtocolProvider.newRPCProtocol4Server(e.getKey()).constructURL(metadata.getUniqueName(), e.getValue()).toString());
		}
		this.metadataWriteService.register(metadata);
	}

	@Override
	public void unregister(ServiceMetadata metadata) throws DiamondException {
		metadataWriteService.unregister(metadata);
	}
	
	/**
	 * 
	 * @param metadata
	 * @return
	 * @throws DiamondException
	 */
	private ServiceAddress metadata2Adress(ServiceMetadata metadata) throws DiamondException {
		ServiceAddress sa = new ServiceAddress(metadata.getUniqueName());
		for(Map.Entry<String, Properties> e : metadata.getExportProtocols().entrySet()) {
			sa.addAddress(Protocol.toEnum(e.getKey()), rpcProtocolProvider.newRPCProtocol4Server(e.getKey()).constructURL(metadata.getUniqueName(), e.getValue()).toString());
		}
		return sa;
	}

}
