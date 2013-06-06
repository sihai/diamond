/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.repository.client.factory;

import com.galaxy.diamond.repository.client.RepositoryClient;

/**
 * 
 * @author sihai
 *
 */
public interface RepositoryClientFactory {

	/**
	 * 
	 * @return
	 */
	RepositoryClient newInstace();
}
