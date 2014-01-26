/**
 * Copyright 2013 Qiangqiang RAO
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.openteach.diamond.router.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.address.AddressingService;
import com.openteach.diamond.address.ServiceAddress;
import com.openteach.diamond.address.listener.Listener;
import com.openteach.diamond.common.lifecycle.AbstractLifeCycle;
import com.openteach.diamond.metadata.ServiceURL;
import com.openteach.diamond.router.RouteParameter;
import com.openteach.diamond.router.ServiceRouter;
import com.openteach.diamond.router.plugin.RouterPlugin;

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
	public List<ServiceURL> route(RouteParameter parameter) {
		return plugin.route(parameter.getServiceName(), parameter.getProtocol());
	}
}
