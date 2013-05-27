/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.provider.impl;

import com.galaxy.hsf.metadata.ServiceMetadata;
import com.galaxy.hsf.provider.ServiceProvider;
import com.galaxy.hsf.provider.factory.ServiceProviderFactory;

/**
 * 
 * @author sihai
 *
 */
public class DefaultServiceProviderFactory implements ServiceProviderFactory {

	@Override
	public ServiceProvider newServiceProvider(ServiceMetadata metadata) {
		ServiceProvider provider = new DefaultServiceProvider(metadata);
		provider.initialize();
		provider.start();
		return provider;
	}

}
