/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service;

import com.galaxy.hsf.common.lifecycle.LifeCycle;
import com.galaxy.hsf.metadata.ServiceMetadata;

/**
 * 
 * @author sihai
 *
 */
public interface ServiceRegister extends LifeCycle {

	/**
	 * 
	 * @param metadata
	 */
	void register(ServiceMetadata metadata);
	
	/**
	 * 
	 * @param metadata
	 */
	void unregister(ServiceMetadata metadata);
}
