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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.diamond.address.AddressingService;
import com.galaxy.diamond.common.exception.DiamondException;
import com.galaxy.diamond.common.lifecycle.AbstractLifeCycle;
import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.diamond.rpc.RPCProtocolProvider;
import com.galaxy.diamond.service.ServiceSubscriber;

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
	protected AddressingService addressingService;
	
	/**
	 * 
	 * @param rpcProtocolProvider
	 * @param readService
	 */
	public AbstractServiceSubscriber(RPCProtocolProvider rpcProtocolProvider, AddressingService addressingService) {
		this.rpcProtocolProvider = rpcProtocolProvider;
		this.addressingService = addressingService;
	}
	
	@Override
	public void subscribe(ServiceMetadata metadata) throws DiamondException {
		// load rpc protocol if need
		for(String protocol : metadata.getImportProtocols().keySet()) {
			rpcProtocolProvider.newRPCProtocol4Client(protocol);
		}
		// subscribe address of this service
		addressingService.addressing(metadata.getUniqueName());
	}

}
