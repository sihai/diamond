/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.repository.client;

import com.galaxy.diamond.repository.client.exception.SequenceNotMatchException;
import com.galaxy.diamond.repository.client.factory.DataFactory;
import com.galaxy.diamond.repository.client.listener.Listener;
import com.galaxy.hsf.common.lifecycle.LifeCycle;

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