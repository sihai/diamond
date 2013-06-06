/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.metadata.impl;

import com.galaxy.diamond.repository.client.RepositoryClient;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;

/**
 * 
 * @author sihai
 *
 */
public class AbstractMetadataService extends AbstractLifeCycle {

	/**
	 * 
	 */
	protected RepositoryClient repositoryClient;
	
	/**
	 * 
	 * @param repositoryClient
	 */
	public AbstractMetadataService(RepositoryClient repositoryClient) {
		this.repositoryClient = repositoryClient;
	}
	
	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}
