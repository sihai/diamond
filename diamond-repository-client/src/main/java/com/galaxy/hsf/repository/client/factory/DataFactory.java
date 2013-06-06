/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.repository.client.factory;

import com.galaxy.hsf.repository.client.Data;
import com.galaxy.hsf.repository.client.Key;

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
	 * @param value
	 * @param sequence -1 for new
	 * @return
	 */
	Data newData(String key, String value, long sequence);
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @param sequence -1 for new
	 * @return
	 */
	Data newData(Key key, String value,  long sequence);
}
