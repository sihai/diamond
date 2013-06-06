/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.address.impl;

import com.galaxy.diamond.metadata.MetadataReadService;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;

/**
 * 
 * @author sihai
 *
 */
public class AbstractAddressingService extends AbstractLifeCycle {

	/**
	 * 
	 */
	protected MetadataReadService metadataReadService;
	
	/**
	 * 
	 * @param metadataReadService
	 */
	public AbstractAddressingService(MetadataReadService metadataReadService) {
		this.metadataReadService = metadataReadService;
	}
	
	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}
