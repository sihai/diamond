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
 */
package com.galaxy.diamond.address.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.galaxy.diamond.address.AddressingService;
import com.galaxy.diamond.address.Protocol;
import com.galaxy.diamond.address.ServiceAddress;
import com.galaxy.diamond.address.listener.Listener;
import com.galaxy.diamond.common.exception.DiamondException;
import com.galaxy.diamond.common.lifecycle.AbstractLifeCycle;
import com.galaxy.diamond.metadata.MetadataReadService;
import com.galaxy.diamond.metadata.ServiceMetadata;

/**
 * 
 * @author sihai
 *
 */
public class DefaultAddressingService extends AbstractLifeCycle implements AddressingService {

	/**
	 * 
	 */
	private MetadataReadService metadataReadService;
	
	/**
	 * 
	 */
	private ConcurrentHashMap<String, ServiceAddress> addressTable;
	
	/**
	 * 
	 */
	private volatile Listener listener;
	
	/**
	 * 
	 * @param metadataReadService
	 */
	public DefaultAddressingService(MetadataReadService metadataReadService) {
		this.metadataReadService = metadataReadService;
	}

	@Override
	public void initialize() {
		super.initialize();
		addressTable = new ConcurrentHashMap<String, ServiceAddress>();
		metadataReadService.register(new com.galaxy.diamond.metadata.Listener() {

			@Override
			public void changed(ServiceMetadata metadata) {
				ServiceAddress address = metadata2Address(metadata);
				if(address.isEmpty()) {
					remove(metadata.getUniqueName());
				} else {
					publish(address);
				}
			}
			
		});
	}

	@Override
	public void destroy() {
		addressTable.clear();
		super.destroy();
	}

	private void publish(ServiceAddress serviceAddress) {
		addressTable.put(serviceAddress.getServiceName(), serviceAddress);
		fire(serviceAddress);
	}

	private void remove(String serviceName) {
		addressTable.remove(serviceName);
		fire(new ServiceAddress(serviceName));
	}

	@Override
	public ServiceAddress addressing(String serviceName) throws DiamondException {
		ServiceAddress address = addressTable.get(serviceName);
		if(null != address) {
			return address;
		}
		ServiceMetadata metadata = metadataReadService.subscribe(serviceName);
		address = metadata2Address(metadata);
		publish(address);
		return address;
	}

	@Override
	public void register(Listener listener) {
		this.listener = listener;
	}
	
	/**
	 * 
	 * @param address
	 */
	private void fire(ServiceAddress serviceAddress) {
		Listener l = listener;
		if(null != l) {
			l.changed(serviceAddress);
		}
	}
	
	/**
	 * 
	 * @param metadata
	 * @return
	 */
	private ServiceAddress metadata2Address(ServiceMetadata metadata) {
		Map<String, List<String>> addressMap = metadata.getAddressMap();
		ServiceAddress address = new ServiceAddress(metadata.getUniqueName());
		for(Map.Entry<String, List<String>> e : addressMap.entrySet()) {
			address.addAddress(Protocol.toEnum(e.getKey()), e.getValue());
		}
		return address;
	}

}
