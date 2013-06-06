/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.metadata.factory;

import com.galaxy.diamond.metadata.MetadataWriteService;
import com.galaxy.hsf.repository.client.RepositoryClient;

/**
 * 
 * @author sihai
 *
 */
public interface MetadataWriteServiceFactory {
	
	/**
	 * 
	 * @param repositoryClient
	 * @return
	 */
	MetadataWriteService newMetadataWriteService(RepositoryClient repositoryClient);
}
