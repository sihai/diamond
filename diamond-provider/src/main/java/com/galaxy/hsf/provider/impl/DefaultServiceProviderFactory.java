/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.provider.impl;

import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.hsf.provider.ServiceProvider;
import com.galaxy.hsf.provider.factory.ServiceProviderFactory;

/**
 * 
 * @author sihai
 *
 */
public class DefaultServiceProviderFactory implements ServiceProviderFactory {

	@Override
	public ServiceProvider newServiceProvider(ServiceMetadata metadata, Object target) {
		DefaultServiceProvider provider = new DefaultServiceProvider(metadata);
		provider.setTarget(target);
		provider.initialize();
		provider.start();
		return provider;
	}

}
