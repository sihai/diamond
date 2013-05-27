/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.repository.client.listener;

import com.galaxy.hsf.repository.client.Key;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractListener implements Listener {

	protected Key key;
	
	public AbstractListener(Key key) {
		this.key = key;
	}

	@Override
	public Key getKey() {
		return key;
	}
}
