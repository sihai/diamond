/**
 * Copyright 2013 openteach
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 */
package com.openteach.diamond.repository.client.cache;

import com.openteach.diamond.repository.client.factory.AbstractCacheFactory;

/**
 * 
 * @author sihai
 *
 * @param <K>
 * @param <V>
 */
public class MemoryLRUCacheFactory<K, V> extends AbstractCacheFactory<K, V> {

	/**
	 * 
	 */
	private int maxEntries = Cache.DEFAULT_MAX_ENTRIES;
	
	/**
	 * 
	 * @param maxEntries
	 * @return
	 */
	public MemoryLRUCacheFactory<K, V> withMaxEntries(int maxEntries) {
		this.maxEntries = maxEntries;
		return this;
	}
	
	@Override
	public Cache<K, V> newInstance() {
		Cache<K, V> cache = new MemoryLRUCache<K, V>(maxEntries);
		cache.register(converter);
		cache.initialize();
		cache.start();
		return cache;
	}
}
