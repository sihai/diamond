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

import java.util.ArrayList;
import java.util.List;

import com.openteach.diamond.common.lifecycle.AbstractLifeCycle;

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
