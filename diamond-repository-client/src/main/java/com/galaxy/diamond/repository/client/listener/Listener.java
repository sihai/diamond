/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.repository.client.listener;

import com.galaxy.diamond.repository.client.DataEvent;
import com.galaxy.diamond.repository.client.Key;

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
