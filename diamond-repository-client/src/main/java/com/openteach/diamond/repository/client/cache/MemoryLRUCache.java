/**
 * Copyright 2013 Qiangqiang RAO
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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU Cache (in memory) 非线程安全
 * @author sihai
 *
 */
public class MemoryLRUCache<K, V> extends AbstractCache<K, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 598432066378598761L;

	/**
	 * 
	 */
	private final int maxEntries;
	
	/**
	 * 
	 */
	private LinkedHashMap<K, V> inernalStore;
	
	/**
	 * 
	 */
	public MemoryLRUCache() {
		this(DEFAULT_MAX_ENTRIES);
	}
	
	/**
	 * 
	 * @param maxEntries
	 */
	public MemoryLRUCache(int maxEntries) {
		this.maxEntries = maxEntries;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		this.inernalStore = new LinkedHashMap<K, V>(maxEntries) {
			protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				boolean isNeedRemove = inernalStore.size() > MemoryLRUCache.this.maxEntries;
				if(isNeedRemove) {
					// fire
					fireEliminated(eldest.getKey(), eldest.getValue());
				}
				return isNeedRemove;
			}
		};
	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		inernalStore.clear();
		inernalStore = null;
	}

	@Override
	public V get(K key) {
		return inernalStore.get(key);
	}

	@Override
	public void put(K key, V value) {
		inernalStore.put(key, value);
	}

	@Override
	public void delete(K key) {
		inernalStore.remove(key);
	}
}
