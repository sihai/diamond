/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service;

import com.galaxy.hsf.common.lifecycle.LifeCycle;

/**
 * 
 * @author sihai
 *
 */
public interface HSFService extends LifeCycle, ServiceRegister, ServiceSubscriber, ServiceInvoker {	
}
