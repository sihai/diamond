/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.address.impl;

import com.galaxy.hsf.address.AddressReadService;
import com.galaxy.hsf.address.AddressWriteService;
import com.galaxy.hsf.address.ServiceAddress;
import com.galaxy.hsf.address.listener.Listener;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.repository.client.RepositoryClient;

/**
 * 
 * @author sihai
 *
 */
public class DefaultAddressService extends AbstractLifeCycle implements AddressReadService, AddressWriteService {

	private RepositoryClient repositoryClient;
	
	/**
	 * 
	 * @param repositoryClient
	 */
	public DefaultAddressService(RepositoryClient repositoryClient) {
		this.repositoryClient = repositoryClient;
	}
	
	@Override
	public void publish(ServiceAddress serviceAddress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(ServiceAddress serviceAddress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServiceAddress getServiceAddress(String serviceName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(Listener listener) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void subscribe(String serviceName) {
		// TODO Auto-generated method stub
		
	}

	public void setRepositoryClient(RepositoryClient repositoryClient) {
		this.repositoryClient = repositoryClient;
	}

}
