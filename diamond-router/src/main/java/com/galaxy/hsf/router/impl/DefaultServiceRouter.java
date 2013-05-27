/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.router.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.address.AddressReadService;
import com.galaxy.hsf.address.ServiceAddress;
import com.galaxy.hsf.address.listener.Listener;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.router.RouteParameter;
import com.galaxy.hsf.router.ServiceRouter;
import com.galaxy.hsf.router.plugin.RouterPlugin;

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
	private AddressReadService addressReadService;
	
	/**
	 * 
	 */
	private RouterPlugin plugin;

	@Override
	public void initialize() {
		
		addressReadService.register(new Listener() {

			@Override
			public void changed(ServiceAddress serviceAddress) {
				logger.info(String.format("Receive service addresses:%s", serviceAddress.toString()));
				if(null != plugin) {
					plugin.changed(serviceAddress);
				}
			}
			
		});
		super.initialize();
		
		if(null != plugin) {
			plugin.initialize();
		}
	}

	@Override
	public void stop() {
		super.stop();
		if(null != plugin) {
			plugin.stop();
		}
	}

	@Override
	public List<String> route(RouteParameter parameter) {
		return plugin.route(parameter.getServiceName(), parameter.getProtocol());
	}

	public void setAddressReadService(AddressReadService addressReadService) {
		this.addressReadService = addressReadService;
	}
	
	public void setPlugin(RouterPlugin plugin) {
		this.plugin = plugin;
	}
}
