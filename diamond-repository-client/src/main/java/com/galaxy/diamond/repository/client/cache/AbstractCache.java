/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.repository.client.cache;

import java.util.ArrayList;
import java.util.List;

import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractCache<K, V> extends AbstractLifeCycle implements Cache<K, V> {

	/**
	 * 
	 */
	private List<ItemEliminateListener<K, V>> listeners = new ArrayList<ItemEliminateListener<K, V>>(2);
	
	/**
	 * 
	 */
	private ValueConverter<V> converter;
	
	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		listeners.clear();
		listeners = null;
	}

	@Override
	public void register(Cache.ItemEliminateListener<K, V> listener) {
		listeners.add(listener);
	}

	@Override
	public void unregister(Cache.ItemEliminateListener<K, V> listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void register(ValueConverter<V> converter) {
		this.converter = converter;
	}
	
	/**
	 * 
	 * @return
	 */
	protected ValueConverter<V> getConverter() {
		return this.converter;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	protected void fireEliminated(K key, V value) {
		for(ItemEliminateListener<K, V> l : listeners) {
			l.onEliminated(key, value);
		}
	}
}
