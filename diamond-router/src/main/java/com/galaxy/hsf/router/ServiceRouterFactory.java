/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.router;

import com.galaxy.hsf.address.AddressingService;
import com.galaxy.hsf.router.plugin.RouterPlugin;

/**
 * 
 * @author sihai
 *
 */
public interface ServiceRouterFactory {

	/**
	 * 
	 * @param addressingService
	 * @param plugin
	 * @return
	 */
	ServiceRouter newServiceRouter(AddressingService addressingService, RouterPlugin plugin);
}
