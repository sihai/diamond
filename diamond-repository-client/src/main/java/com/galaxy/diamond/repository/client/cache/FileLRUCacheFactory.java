/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.repository.client.cache;

import com.galaxy.diamond.repository.client.factory.AbstractCacheFactory;

/**
 * 
 * @author sihai
 *
 * @param <K>
 * @param <V>
 */
public class FileLRUCacheFactory<K, V> extends AbstractCacheFactory<K, V> {

	/**
	 * 
	 */
	private int maxEntries = Cache.DEFAULT_MAX_ENTRIES;
	
	/**
	 * 
	 */
	private String fileName;
	
	/**
	 * 
	 * @param maxEntries
	 * @return
	 */
	public FileLRUCacheFactory<K, V> withMaxEntries(int maxEntries) {
		this.maxEntries = maxEntries;
		return this;
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public FileLRUCacheFactory<K, V> withFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}
	
	@Override
	public Cache<K, V> newInstance() {
		Cache<K, V> cache = new FileLRUCache<K, V>(maxEntries, fileName);
		cache.register(converter);
		cache.initialize();
		cache.start();
		return cache;
	}
}
