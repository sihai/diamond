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
package com.openteach.diamond.repository.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.openteach.diamond.common.lifecycle.AbstractLifeCycle;
import com.openteach.diamond.repository.client.cache.Cache;
import com.openteach.diamond.repository.client.exception.SequenceNotMatchException;
import com.openteach.diamond.repository.client.factory.DummyCacheFactory;
import com.openteach.diamond.repository.client.listener.Listener;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractRepositoryClient extends AbstractLifeCycle implements RepositoryClient {

	/**
	 * Certificate for access repository server
	 */
	protected Certificate certificate;
	
	/**
	 * 
	 */
	private Map<Key, Listener> listenerMap = Collections.synchronizedMap(new HashMap<Key, Listener>());
	
	/**
	 * 
	 */
	private Cache<Key, Object> localCache = new DummyCacheFactory<Key, Object>().newInstance();
	
	/**
	 * 
	 */
	public AbstractRepositoryClient(Certificate certificate) {
		try {
			this.certificate = certificate.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param localCache
	 */
	public AbstractRepositoryClient(Certificate certificate, Cache<Key, Object> localCache) {
		try {
			this.certificate = certificate.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		this.localCache = localCache;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		validateCertificate();
		//this.localCache.initialize();
	}

	@Override
	public void start() {
		super.start();
		//this.localCache.start();
	}

	@Override
	public void stop() {
		super.stop();
		this.localCache.stop();
	}
	
	@Override
	public void destroy() {
		super.destroy();
		this.localCache.destroy();
	}
	
	@Override
	public Key newKey(String key) {
		return newKey(key, Key.NONE_SUB_KEY);
	}
	
	@Override
	public Key newKey(String key, String subKey) {
		return newKey(key, subKey, Key.INIT_SEQUENCE);
	}
	
	@Override
	public Key newKey(String key, String subKey, long sequence) {
		return new Key(this.certificate.getNamespace(), key, subKey, sequence);
	}

	@Override
	public Data newData(String key, String value) {
		return newData(key, Key.NONE_SUB_KEY, value);
	}
	
	@Override
	public Data newData(String key, String subKey, String value) {
		return newData(key, subKey, Key.INIT_SEQUENCE, value);
	}
	
	@Override
	public Data newData(String key, String subKey, long sequence, String value) {
		return newData(newKey(key, subKey, sequence), value);
	}
	
	@Override
	public Data newData(Key key, String value) {
		return new Data(key, value);
	}

	@Override
	public Data get(Key key) {
		Data data = (Data)localCache.get(key);
		if(null == data) {
			data = getFromServer(key);
			localCache.put(key, data);
		}
		return data;
	}

	@Override
	public Data get(Key key, Listener listener) {
		Data data = get(key);
		register(key, listener);
		return data;
	}
	
	@Override
	public List<Data> mget(Key key) {
		List<Data> dList = null;
		List<Key> keyList = (List<Key>)localCache.get(key);
		if(null == keyList) {
			keyList = new ArrayList<Key>();
			dList = mgetFromServer(key);
			for(Data d : dList) {
				localCache.put(d.getKey(), d);
				keyList.add(d.getKey());
			}
			localCache.put(key, keyList);
		} else {
			dList = new ArrayList<Data>();
			for(Key k : keyList) {
				dList.add(get(k));
			}
		}
		return dList;
	}

	@Override
	public List<Data> mget(Key key, Listener listener) {
		List<Data> dList = mget(key);
		for(Data d : dList) {
			register(d.getKey(), listener);
		}
		return dList;
	}

	@Override
	public void put(Data data) throws SequenceNotMatchException {
		// 1. put in remote server second
		put2Server(data);
		// 2. put in local cache first
		localCache.put(data.getKey(), data);
	}

	@Override
	public void delete(Key key) throws SequenceNotMatchException {
		// 1. delete in local cache first
		localCache.delete(key);
		// 2. delete in remote server second
		deleteFromServer(key);
	}

	@Override
	public void register(Key key, Listener listener) {
		listenerMap.put(key, listener);
	}

	@Override
	public void unregister(Key key) {
		listenerMap.remove(key);
	}
	
	/**
	 * Dispatch data changed event
	 * @param event
	 */
	protected void fire(DataEvent event) {
		// dispatch event
		Listener l = listenerMap.get(event.getOldOne().getKey());
		if(null != l) {
			l.changed(event);
		}
	}
	
	/**
	 * Get data from remote server
	 * @param key
	 * @return
	 */
	protected abstract Data getFromServer(Key key);
	
	/**
	 * Get all data from remote server (subkey equals null)
	 * @param key
	 * @return
	 */
	protected abstract List<Data> mgetFromServer(Key key);
	
	/**
	 * Put data into remote server
	 * @param data
	 * @throws SequenceNotMatchException
	 */
	protected abstract void put2Server(Data data) throws SequenceNotMatchException;
	
	/**
	 * Delete data in remote server
	 * @param key
	 * @throws SequenceNotMatchException
	 */
	protected abstract void deleteFromServer(Key key) throws SequenceNotMatchException;
	
	/**
	 * 
	 * @param data
	 */
	protected abstract void monitor(Data data);
	
	/**
	 * 
	 * @param data
	 */
	protected abstract void unmonitor(Data data);
	
	/**
	 * 
	 */
	private void validateCertificate() {
		
		//XXX FIXME get namespace 
		certificate.setNamespace(certificate.getAppName());
	}
}
