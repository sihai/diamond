/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service;

import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.hsf.common.lifecycle.LifeCycle;

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
