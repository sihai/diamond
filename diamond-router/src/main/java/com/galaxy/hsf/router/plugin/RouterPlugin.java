/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.router.plugin;

import java.util.List;

import com.galaxy.hsf.address.Protocol;
import com.galaxy.hsf.address.ServiceAddress;
import com.galaxy.hsf.common.lifecycle.LifeCycle;

/**
 * 
 * @author sihai
 *
 */
public interface RouterPlugin extends LifeCycle {

	/**
	 * 
	 * @param serviceAddress
	 */
	void changed(ServiceAddress serviceAddress);
	
	/**
	 * 
	 * @param serviceName
	 * @param protocol
	 * @return
	 */
	List<String> route(String serviceName, Protocol protocol);
}
