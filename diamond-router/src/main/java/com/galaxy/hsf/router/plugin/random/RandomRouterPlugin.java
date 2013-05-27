/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.router.plugin.random;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.galaxy.hsf.address.Protocol;
import com.galaxy.hsf.address.ServiceAddress;
import com.galaxy.hsf.router.plugin.AbstractRouterPlugin;

/**
 * 
 * @author sihai
 *
 */
public class RandomRouterPlugin extends AbstractRouterPlugin {

	/**
	 * 
	 */
	private Map<String, Map<Protocol, List<String>>> table = new HashMap<String, Map<Protocol, List<String>>>();
	
	/**
	 * 
	 */
	private ReadWriteLock _rw_lock_ = new ReentrantReadWriteLock();
	
	@Override
	public void changed(ServiceAddress serviceAddress) {
		try {
			_rw_lock_.writeLock().lock();
			Map<Protocol, List<String>> addressMap = table.get(serviceAddress.getServiceName());
			if(null == addressMap) {
				addressMap = new HashMap<Protocol, List<String>>();
				table.put(serviceAddress.getServiceName(), addressMap);
			}
			addressMap.putAll(serviceAddress.getAllAddresses());
		} finally {
			_rw_lock_.writeLock().unlock();
		}

	}

	@Override
	public List<String> route(String serviceName, Protocol protocol) {
		try {
			_rw_lock_.readLock().lock();
			Map<Protocol, List<String>> addressMap = table.get(serviceName);
			if(null == addressMap) {
				return Collections.EMPTY_LIST;
			}
			List<String> addresses = addressMap.get(protocol);
			if(null == addresses) {
				return Collections.EMPTY_LIST;
			}
			return addresses;
		} finally {
			_rw_lock_.readLock().unlock();
		}

	}

}
