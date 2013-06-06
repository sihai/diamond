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
public interface AddressingService extends LifeCycle {
	
	/**
	 * 寻址
	 * @param serviceName
	 * @return
	 */
	ServiceAddress addressing(String serviceName);
	
	/**
	 * Register one listener for all address change
	 * @param listener
	 */
	void register(Listener listener);
}
