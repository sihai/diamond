/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.address.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.galaxy.diamond.metadata.MetadataReadService;
import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.hsf.address.AddressingService;
import com.galaxy.hsf.address.Protocol;
import com.galaxy.hsf.address.ServiceAddress;
import com.galaxy.hsf.address.listener.Listener;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;

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
	public ServiceAddress addressing(String serviceName) throws HSFException {
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
