/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.router.impl;

import com.galaxy.hsf.address.AddressingService;
import com.galaxy.hsf.router.ServiceRouter;
import com.galaxy.hsf.router.ServiceRouterFactory;
import com.galaxy.hsf.router.plugin.RouterPlugin;

/**
 * 
 * @author sihai
 *
 */
public class DefaultServiceRouterFactory implements ServiceRouterFactory {

	@Override
	public ServiceRouter newServiceRouter(AddressingService addressingService, RouterPlugin plugin) {
		DefaultServiceRouter router = new DefaultServiceRouter(addressingService, plugin);
		router.initialize();
		router.start();
		return router;
	}

}
