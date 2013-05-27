/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.address.listener;

import com.galaxy.hsf.address.ServiceAddress;

/**
 * 
 * @author sihai
 *
 */
public interface Listener {

	/**
	 * 
	 * @param serviceAddress
	 */
	void changed(ServiceAddress serviceAddress);
}
