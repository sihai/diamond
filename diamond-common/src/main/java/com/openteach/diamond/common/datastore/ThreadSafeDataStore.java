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
 */
package com.openteach.diamond.common.datastore;

import java.util.concurrent.ConcurrentHashMap;

import com.openteach.diamond.common.ThreadSafe;

/**
 * 
 * @author sihai
 *
 */
@ThreadSafe
public class ThreadSafeDataStore implements DataStore {

	/**
	 * 
	 */
	private ConcurrentHashMap <String/**组件类名*/, ConcurrentHashMap<String/**数据名*/, Object/**数据值*/>> storage = new ConcurrentHashMap<String, ConcurrentHashMap<String,Object>>();

    public <T> T get(String componentName, String key) {
    	ConcurrentHashMap<String, Object> subStorage = storage.get(componentName);
        return null == subStorage ? null : (T)subStorage.get(key);
    }

    public void put(String componentName, String key, Object value) {
    	ConcurrentHashMap<String, Object> subStorage = null;
        if(!storage.containsKey(componentName)){
        	subStorage = new ConcurrentHashMap<String, Object>();
        } else {
        	subStorage = storage.get(componentName);
        }
        subStorage.put(key, value);
        ConcurrentHashMap<String, Object> old = storage.putIfAbsent(componentName, subStorage);
        if(null != old) {
        	// some body put sub storage yet
        	old.put(key, value);
        	subStorage.clear();
        }
    }
    
    public <T> T putIfAbsent(String componentName, String key, Object value) {
    	ConcurrentHashMap<String, Object> subStorage = null;
        if(!storage.containsKey(componentName)){
        	subStorage = new ConcurrentHashMap<String, Object>();
        } else {
        	subStorage = storage.get(componentName);
        }
        T oldValue = (T)subStorage.putIfAbsent(key, value);
        ConcurrentHashMap<String, Object> old = storage.putIfAbsent(componentName, subStorage);
        if(null != old) {
        	// some body put sub storage yet
        	old.put(key, value);
        	subStorage.clear();
        }
        return oldValue;
    }

    public void remove(String componentName, String key) {
        if(!storage.containsKey(componentName)){
            return;
        }
        storage.get(componentName).remove(key);
    }
}