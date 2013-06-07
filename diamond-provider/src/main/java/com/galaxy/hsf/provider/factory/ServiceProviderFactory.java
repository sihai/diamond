/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.provider.factory;

import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.hsf.provider.ServiceProvider;

/**
 * 
 * @author sihai
 *
 */
public interface ServiceProviderFactory {
	
	/**
	 * 
	 * @param metadata
	 * @param target
	 * @return
	 */
	ServiceProvider newServiceProvider(ServiceMetadata metadata, Object target);
}
