/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.consumer;

import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractServiceConsumer extends AbstractLifeCycle implements ServiceConsumer {

	/**
	 * 
	 */
	protected ServiceMetadata metadata = new ServiceMetadata();

	@Override
	public ServiceMetadata getMetadata() {
		return metadata;
	}	
	
}
