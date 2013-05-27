/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.address;

import com.galaxy.hsf.common.lifecycle.LifeCycle;

/**
 * 
 * @author sihai
 *
 */
public interface AddressWriteService extends LifeCycle {

	/**
	 * Publish addresses of one service to repository
	 * @param serviceAddress
	 */
	void publish(ServiceAddress serviceAddress);
	
	/**
	 * 
	 * @param serviceAddress
	 */
	void remove(ServiceAddress serviceAddress);
}
