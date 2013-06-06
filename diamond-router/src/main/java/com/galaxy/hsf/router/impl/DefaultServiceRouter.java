/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.router.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.address.AddressingService;
import com.galaxy.hsf.address.ServiceAddress;
import com.galaxy.hsf.address.listener.Listener;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.router.RouteParameter;
import com.galaxy.hsf.router.ServiceRouter;
import com.galaxy.hsf.router.plugin.RouterPlugin;
import com.galaxy.hsf.router.plugin.random.RandomRouterPlugin;

/**
 * 
 * @author sihai
 *
 */
public class DefaultServiceRouter extends AbstractLifeCycle implements ServiceRouter {

	private static final Log logger = LogFactory.getLog(DefaultServiceRouter.class);
	/**
	 * 
	 */
	private AddressingService addressingService;
	
	/**
	 * 
	 */
	private RouterPlugin plugin;
	
	/**
	 * 
	 * @param plugin
	 */
	public DefaultServiceRouter(AddressingService addressingService, RouterPlugin plugin) {
		this.addressingService = addressingService;
		this.plugin = plugin;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		addressingService.register(new Listener() {

			@Override
			public void changed(ServiceAddress serviceAddress) {
				logger.info(String.format("Receive service addresses:%s", serviceAddress.toString()));
				if(null != plugin) {
					plugin.changed(serviceAddress);
				}
			}
			
		});
	}

	@Override
	public void stop() {
		super.stop();
	}

	@Override
	public List<String> route(RouteParameter parameter) {
		return plugin.route(parameter.getServiceName(), parameter.getProtocol());
	}
}
