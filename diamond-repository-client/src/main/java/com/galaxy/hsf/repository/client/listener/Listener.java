/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.repository.client.listener;

import com.galaxy.hsf.repository.client.DataEvent;
import com.galaxy.hsf.repository.client.Key;

/**
 * 
 * @author sihai
 *
 */
public interface Listener {
	
	/**
	 * Get Key of data this listener listen
	 * @return
	 */
	Key getKey();
	
	/**
	 * Dispatch change
	 * @param event
	 */
	void changed(DataEvent event);
}
