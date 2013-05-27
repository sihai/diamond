/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.address;

import com.galaxy.hsf.address.listener.Listener;
import com.galaxy.hsf.common.lifecycle.LifeCycle;

/**
 * 
 * @author sihai
 *
 */
public interface AddressReadService extends LifeCycle {

	/**
	 * Get addresses for one service
	 * @param serviceName
	 * @return
	 */
	ServiceAddress getServiceAddress(String serviceName);
	
	/**
	 * 
	 * @param serviceName
	 */
	void subscribe(String serviceName);
	
	/**
	 * Register one listener for all address change
	 * @param listener
	 */
	void register(Listener listener);
}
