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
package com.openteach.diamond.repository.client.factory;

import com.openteach.diamond.repository.client.Data;
import com.openteach.diamond.repository.client.Key;

/**
 * 
 * @author sihai
 *
 */
public interface DataFactory {
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	Key newKey(String key);
	
	/**
	 * 
	 * @param key
	 * @param subKey
	 * @return
	 */
	Key newKey(String key, String subKey);
	
	/**
	 * 
	 * @param key
	 * @param subKey
	 * @param sequence Key.INIT_SEQUENCE for new
	 * @return
	 */
	Key newKey(String key, String subKey, long sequence);
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	Data newData(String key, String value);
	
	/**
	 * 
	 * @param key
	 * @param subKey
	 * @param value
	 * @return
	 */
	Data newData(String key, String subKey, String value);
	
	/**
	 * 
	 * @param key
	 * @param subKey
	 * @param sequence Key.INIT_SEQUENCE for new
	 * @param value
	 * @return
	 */
	Data newData(String key, String subKey, long sequence, String value);
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	Data newData(Key key, String value);
}
