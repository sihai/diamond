/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.repository.client.factory;

import com.galaxy.hsf.repository.client.RepositoryClient;

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
