/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.address.factory;

import com.galaxy.diamond.metadata.MetadataReadService;
import com.galaxy.hsf.address.AddressingService;
import com.galaxy.hsf.address.impl.DefaultAddressingService;

/**
 * 
 * @author sihai
 *
 */
public class DefaultAddressingServiceFactory implements AddressingServiceFactory {

	@Override
	public AddressingService newAddressingService(MetadataReadService metadataReadService) {
		DefaultAddressingService addressingService = new DefaultAddressingService(metadataReadService);
		addressingService.initialize();
		addressingService.start();
		return null;
	}

}
