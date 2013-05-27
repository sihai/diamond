/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.repository.client.factory;

import com.galaxy.hsf.repository.client.cache.Cache;

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
