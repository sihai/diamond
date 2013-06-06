/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.metadata.factory;

import com.galaxy.diamond.metadata.MetadataReadService;
import com.galaxy.diamond.repository.client.RepositoryClient;

/**
 * 
 * @author sihai
 *
 */
public interface MetadataReadServiceFactory {

	/**
	 * 
	 * @param repositoryClient
	 * @return
	 */
	MetadataReadService newMetadataReadService(RepositoryClient repositoryClient);

}
