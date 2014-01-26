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
package com.openteach.diamond.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.common.exception.DiamondException;
import com.openteach.diamond.common.lifecycle.AbstractLifeCycle;
import com.openteach.diamond.metadata.MetadataWriteService;
import com.openteach.diamond.metadata.ServiceMetadata;
import com.openteach.diamond.metadata.ServiceURL;
import com.openteach.diamond.rpc.RPCProtocolProvider;
import com.openteach.diamond.service.MethodInvoker;
import com.openteach.diamond.service.ServiceRegister;

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
		List<ServiceURL> sList = new ArrayList<ServiceURL>();
		for(Map.Entry<String, Map<String, String>> e : metadata.getExportProtocols().entrySet()) {
			sList.add(rpcProtocolProvider.newRPCProtocol4Server(e.getKey()).constructURL(metadata.getUniqueName(), e.getValue()));
		}
		this.metadataWriteService.register(metadata, sList);
	}

	@Override
	public void unregister(ServiceMetadata metadata) throws DiamondException {
		metadataWriteService.unregister(metadata);
	}
}
