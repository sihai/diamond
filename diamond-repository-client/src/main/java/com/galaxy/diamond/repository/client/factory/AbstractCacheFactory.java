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
public abstract class AbstractCacheFactory<K, V> implements CacheFactory<K, V> {

	/**
	 * 
	 */
	protected Cache.ValueConverter<V> converter;
	
	/**
	 * 
	 * @param converter
	 * @return
	 */
	public AbstractCacheFactory<K, V> withValueConverter(Cache.ValueConverter<V> converter) {
		this.converter = converter;
		return this;
	}
}
