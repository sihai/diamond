/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.repository.client;

import com.galaxy.hsf.common.lifecycle.LifeCycle;
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
	 */
	void put(Data data);
	
	/**
	 * Delete one data local and remote server
	 * @param key
	 */
	void delete(Key key);
	
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