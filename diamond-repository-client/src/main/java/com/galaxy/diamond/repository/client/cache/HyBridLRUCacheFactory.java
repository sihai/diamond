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
public class HyBridLRUCacheFactory<K, V> extends AbstractCacheFactory<K, V> {

	/**
	 * 
	 */
	private int maxEntries = Cache.DEFAULT_MAX_ENTRIES;
	
	/**
	 * 
	 */
	private int maxEntriesInMemory = Cache.DEFAULT_MAX_ENTRIES / 2;
	
	/**
	 * 
	 */
	private String fileName;
	
	/**
	 * 
	 * @param maxEntries
	 * @return
	 */
	public HyBridLRUCacheFactory<K, V> withMaxEntries(int maxEntries) {
		this.maxEntries = maxEntries;
		return this;
	}
	
	/**
	 * 
	 * @param maxEntries
	 * @return
	 */
	public HyBridLRUCacheFactory<K, V> withMaxEntriesInMemory(int maxEntriesInMemory) {
		this.maxEntriesInMemory = maxEntriesInMemory;
		return this;
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public HyBridLRUCacheFactory<K, V> withFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}
	
	@Override
	public Cache<K, V> newInstance() {
		Cache<K, V> cache = new HyBridLRUCache<K, V>(maxEntriesInMemory, maxEntries, fileName);
		cache.register(converter);
		cache.initialize();
		cache.start();
		return cache;
	}
}
