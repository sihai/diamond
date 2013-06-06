/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.metadata.factory;

import com.galaxy.diamond.metadata.MetadataWriteService;
import com.galaxy.diamond.metadata.impl.DefaultMetadataWriteService;
import com.galaxy.hsf.repository.client.RepositoryClient;

/**
 * 
 * @author sihai
 *
 */
public class DefaultMetadataWriteServiceFactory implements MetadataWriteServiceFactory {
	
	@Override
	public MetadataWriteService newMetadataWriteService(RepositoryClient repositoryClient) {
		DefaultMetadataWriteService instance = new DefaultMetadataWriteService(repositoryClient);
		instance.initialize();
		instance.start();
		return instance;
	}

}
