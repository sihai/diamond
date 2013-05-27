/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.metadata.impl;

import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.metadata.MetadataReadService;
import com.galaxy.hsf.metadata.MetadataWriteService;
import com.galaxy.hsf.metadata.PublishHook;
import com.galaxy.hsf.metadata.ServiceMetadata;
import com.galaxy.hsf.repository.client.RepositoryClient;

/**
 * 
 * @author sihai
 *
 */
public class DefaultMetadataService extends AbstractLifeCycle implements MetadataReadService, MetadataWriteService {

	/**
	 * 
	 */
	private RepositoryClient repositoryClient;
	
	@Override
	public void register(ServiceMetadata metadata) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregister(ServiceMetadata metadata) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPublishHook(PublishHook hook) {
		// TODO Auto-generated method stub

	}

	@Override
	public void subscriber(ServiceMetadata metadata) {
		// TODO Auto-generated method stub

	}

}
