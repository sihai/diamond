/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.metadata.factory;

import com.galaxy.diamond.metadata.MetadataReadService;
import com.galaxy.diamond.metadata.impl.DefaultMetadataReadService;
import com.galaxy.hsf.repository.client.RepositoryClient;

/**
 * 
 * @author sihai
 *
 */
public class DefaultMetadataReadServiceFactory implements MetadataReadServiceFactory {
	
	@Override
	public MetadataReadService newMetadataReadService(RepositoryClient repositoryClient) {
		DefaultMetadataReadService instance = new DefaultMetadataReadService(repositoryClient);
		instance.initialize();
		instance.start();
		return instance;
	}

}
