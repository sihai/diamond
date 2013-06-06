/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.metadata.impl;

import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.repository.client.RepositoryClient;

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
