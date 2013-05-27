/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import com.galaxy.hsf.service.ServiceInvoker;
import com.galaxy.hsf.service.ServiceRegister;
import com.galaxy.hsf.service.ServiceSubscriber;

/**
 * 
 * @author sihai
 *
 */
public class DefaultHSFService extends AbstractHSFService {

	/**
	 * 
	 * @param serviceRegister
	 * @param serviceSubscriber
	 * @param serviceInvoker
	 */
	public DefaultHSFService(ServiceRegister serviceRegister, ServiceSubscriber serviceSubscriber, ServiceInvoker serviceInvoker) {
		super(serviceRegister, serviceSubscriber, serviceInvoker);
	}
}
