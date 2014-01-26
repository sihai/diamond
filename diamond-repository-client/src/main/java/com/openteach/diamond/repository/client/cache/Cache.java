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

import com.openteach.diamond.common.lifecycle.AbstractLifeCycle;
import com.openteach.diamond.common.lifecycle.LifeCycle;

/**
 * 
 * @author sihai
 *
 * @param <K>
 * @param <T>
 */
public interface Cache<K, V> extends LifeCycle {
	
	//==================================================================
	//							Constants
	//==================================================================
	int DEFAULT_MAX_ENTRIES = 1024;
	
	//==================================================================
	//							Methods
	//==================================================================
	/**
	 * Get value from cache by key
	 * @param key
	 * @return
	 */
	V get(K key);
	
	/**
	 * Put one Key->Value in cache
	 * @param key
	 * @param value
	 * @return
	 */
	void put(K key, V value);
	
	/**
	 * Delete one in cache
	 * @param key
	 */
	void delete(K key);
	
	/**
	 * 
	 * @param listener
	 */
	void register(ItemEliminateListener<K, V> listener);
	
	/**
	 * 
	 * @param listener
	 */
	void unregister(ItemEliminateListener<K, V> listener);
	
	/**
	 * 
	 * @param converter
	 */
	void register(ValueConverter<V> converter);
	
	//==================================================================
	//							Inner class
	//==================================================================
	enum Type {
		DUMMY,
		MEMORY_LRU,
		FILE_LRU,
		HYBRID_LRU;
	}
	/**
	 * 
	 * @author sihai
	 *
	 */
	interface ItemEliminateListener<K, V> {
		/**
		 * 
		 * @param key
		 * @param v
		 */
		void onEliminated(K key, V v);
	}
	
	interface ValueConverter<V> {
		
		/**
		 * 
		 * @param v
		 * @return
		 */
		byte[] toBytes(V v);
		
		/**
		 * 
		 * @param bytes
		 * @return
		 */
		V fromBytes(byte[] bytes);
	}
	
	class DummyCache<K, V> extends AbstractLifeCycle implements Cache<K, V> {

		@Override
		public Object get(Object key) {
			return null;
		}

		@Override
		public void put(Object key, Object value) {
		}

		@Override
		public void delete(K key) {}

		@Override
		public void register(ItemEliminateListener<K, V> listener) {
		}

		@Override
		public void unregister(ItemEliminateListener<K, V> listener) {
		}

		@Override
		public void register(ValueConverter<V> converter) {
		}
	};
}
