/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.provider.factory;

import com.galaxy.hsf.metadata.ServiceMetadata;
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
	 * @return
	 */
	ServiceProvider newServiceProvider(ServiceMetadata metadata);
}
