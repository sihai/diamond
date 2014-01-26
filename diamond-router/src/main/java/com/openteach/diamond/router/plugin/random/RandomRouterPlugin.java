/**
 * Copyright 2013 openteach
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
package com.openteach.diamond.router.plugin.random;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.openteach.diamond.address.Protocol;
import com.openteach.diamond.address.ServiceAddress;
import com.openteach.diamond.metadata.ServiceURL;
import com.openteach.diamond.router.plugin.AbstractRouterPlugin;

/**
 * 
 * @author sihai
 *
 */
public class RandomRouterPlugin extends AbstractRouterPlugin {

	/**
	 * 
	 */
	private Map<String, Map<Protocol, List<ServiceURL>>> table = new HashMap<String, Map<Protocol, List<ServiceURL>>>();
	
	/**
	 * 
	 */
	private ReadWriteLock _rw_lock_ = new ReentrantReadWriteLock();
	
	@Override
	public void changed(ServiceAddress serviceAddress) {
		try {
			_rw_lock_.writeLock().lock();
			Map<Protocol, List<ServiceURL>> addressMap = table.get(serviceAddress.getServiceName());
			if(null == addressMap) {
				addressMap = new HashMap<Protocol, List<ServiceURL>>();
				table.put(serviceAddress.getServiceName(), addressMap);
			}
			addressMap.putAll(serviceAddress.getAllAddresses());
		} finally {
			_rw_lock_.writeLock().unlock();
		}

	}

	@Override
	public List<ServiceURL> route(String serviceName, Protocol protocol) {
		try {
			_rw_lock_.readLock().lock();
			Map<Protocol, List<ServiceURL>> addressMap = table.get(serviceName);
			if(null == addressMap) {
				return Collections.EMPTY_LIST;
			}
			List<ServiceURL> addresses = addressMap.get(protocol);
			if(null == addresses) {
				return Collections.EMPTY_LIST;
			}
			return addresses;
		} finally {
			_rw_lock_.readLock().unlock();
		}

	}

}
