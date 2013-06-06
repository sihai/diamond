/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.repository.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.galaxy.diamond.repository.client.cache.Cache;
import com.galaxy.diamond.repository.client.exception.SequenceNotMatchException;
import com.galaxy.diamond.repository.client.factory.DummyCacheFactory;
import com.galaxy.diamond.repository.client.listener.Listener;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;

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
	private Cache<Key, Data> localCache = new DummyCacheFactory<Key, Data>().newInstance();
	
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
	public AbstractRepositoryClient(Certificate certificate, Cache<Key, Data> localCache) {
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
		this.localCache.initialize();
	}

	@Override
	public void start() {
		super.start();
		this.localCache.start();
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
		return new Key(this.certificate.getNamespace(), key, -1);
	}

	@Override
	public Data newData(String key, String value, long sequence) {
		return newData(newKey(key), value, sequence);
	}

	@Override
	public Data newData(Key key, String value, long sequence) {
		return new Data(key, value, sequence);
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
