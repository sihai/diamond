/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.common.datastore;

import java.util.concurrent.ConcurrentHashMap;

import com.galaxy.hsf.common.ThreadSafe;

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