/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.address.factory;

import com.galaxy.diamond.metadata.MetadataReadService;
import com.galaxy.hsf.address.AddressingService;

/**
 * 
 * @author sihai
 *
 */
public interface AddressingServiceFactory {
	
	/**
	 * 
	 * @param metadataReadService
	 * @return
	 */
	AddressingService newAddressingService(MetadataReadService metadataReadService);
}
