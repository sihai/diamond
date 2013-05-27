/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.repository.client.cache;

import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.common.lifecycle.LifeCycle;

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
