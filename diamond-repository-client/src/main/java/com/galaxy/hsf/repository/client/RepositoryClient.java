/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.repository.client;

import com.galaxy.hsf.common.lifecycle.LifeCycle;
import com.galaxy.hsf.repository.client.exception.SequenceNotMatchException;
import com.galaxy.hsf.repository.client.factory.DataFactory;
import com.galaxy.hsf.repository.client.listener.Listener;

/**
 * 
 * @author sihai
 *
 */
public interface RepositoryClient extends DataFactory, LifeCycle {
	
	//===================================================================
	//					Data interface
	//===================================================================
	/**
	 * Get data of this key
	 * @param key
	 * @return
	 */
	Data get(Key key);
	
	/**
	 * Get data of this key and register listener on this key
	 * @param key
	 * @param listener
	 * @return
	 */
	Data get(Key key, Listener listener);
	
	/**
	 * Put one data into repository
	 * @param data
	 * @throws SequenceNotMatchException
	 */
	void put(Data data) throws SequenceNotMatchException;
	
	/**
	 * Delete one data local and remote server
	 * @param key
	 * @throws SequenceNotMatchException
	 */
	void delete(Key key) throws SequenceNotMatchException;
	
	//===================================================================
	//					Listener interface
	//===================================================================
	/**
	 * Register one listener on one key
	 * @param key
	 * @param listener
	 */
	void register(Key key, Listener listener);
	
	/**
	 * Unregister one listener on one key
	 * @param key
	 */
	void unregister(Key key);
}