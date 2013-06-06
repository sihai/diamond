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
 * @param <K>
 * @param <V>
 */
public class DummyCacheFactory<K, V> implements CacheFactory<K, V> {

	@Override
	public Cache<K, V> newInstance() {
		return new Cache.DummyCache<K, V>();
	}
}
