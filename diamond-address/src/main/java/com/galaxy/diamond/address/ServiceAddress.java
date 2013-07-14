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
package com.galaxy.diamond.address;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represent of a address for one service
 * 
 * @author sihai
 *
 */
public class ServiceAddress {

	/**
	 * Name of service
	 */
	private String serviceName;
	
	/**
	 * All addresses of this service
	 */
	private Map<Protocol, List<String>> addressMap = new HashMap<Protocol, List<String>>(Protocol.values().length);

	/**
	 * 
	 * @param serviceName
	 */
	public ServiceAddress(String serviceName) {
		this.serviceName = serviceName;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getServiceName() {
		return serviceName;
	}
	
	/**
	 * 
	 * @param protocol
	 * @param address
	 * @return
	 */
	public ServiceAddress addAddress(Protocol protocol, String address) {
		List<String> addressList = addressMap.get(protocol);
		if(null == addressList) {
			addressList = new ArrayList<String>(1);
			addressMap.put(protocol, addressList);
		}
		addressList.add(address);
		return this;
	}
	
	/**
	 * 
	 * @param protocol
	 * @param addressList
	 * @return
	 */
	public ServiceAddress addAddress(Protocol protocol, List<String> addressList) {
		List<String> list = addressMap.get(protocol);
		if(null == list) {
			list = new ArrayList<String>(1);
			addressMap.put(protocol, list);
		}
		list.addAll(addressList);
		return this;
	}
	
	/**
	 * 
	 * @param protocol
	 * @return
	 */
	public List<String> getAddresses(Protocol protocol) {
		List<String> addressList = addressMap.get(protocol);
		return null == addressList ? Collections.EMPTY_LIST : Collections.unmodifiableList(addressList);
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<Protocol, List<String>> getAllAddresses() {
		return Collections.unmodifiableMap(addressMap);
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<Protocol> getSupportedProtocols() {
		return Collections.unmodifiableSet(addressMap.keySet());
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return addressMap.isEmpty();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("===============================================================================").append("\n");
		sb.append("	ServiceName:").append(serviceName).append("\n");
		for(Map.Entry<Protocol, List<String>> e : addressMap.entrySet()) {
			sb.append("		Protocol:").append(e.getKey().getName()).append("\n");
			sb.append("			Addresses:").append("\n");
			for(String address : e.getValue()) {
				sb.append("				").append(address).append("\n");
			}
		}
		sb.append("===============================================================================").append("\n");
		return sb.toString();
	}
}
