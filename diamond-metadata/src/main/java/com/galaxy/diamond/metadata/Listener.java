/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.metadata;

/**
 * 
 * @author sihai
 *
 */
public interface Listener {

	/**
	 * 
	 * @param metadata
	 */
	void changed(ServiceMetadata metadata);
}
