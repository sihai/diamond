/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.repository.client.factory;

import com.galaxy.diamond.repository.client.cache.Cache;

/**
 * 
 * @author sihai
 *
 */
public interface CacheFactory<K, V> {
	
	/**
	 * 
	 * @return
	 */
	Cache<K, V> newInstance();
}
