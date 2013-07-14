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
package com.galaxy.diamond.repository.client;

import com.galaxy.diamond.common.lifecycle.LifeCycle;
import com.galaxy.diamond.repository.client.exception.SequenceNotMatchException;
import com.galaxy.diamond.repository.client.factory.DataFactory;
import com.galaxy.diamond.repository.client.listener.Listener;

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