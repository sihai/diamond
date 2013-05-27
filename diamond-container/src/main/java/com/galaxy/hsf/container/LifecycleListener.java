/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.container;

import com.galaxy.hsf.common.exception.HSFException;

/**
 * 
 * @author sihai
 *
 */
public interface LifecycleListener {

	public static final String BEFORE_START_EVENT = "before_start";
	
	public static final String AFTER_START_EVENT = "after_start";
	
	public static final String BEFORE_STOP_EVENT = "before_stop";
	
	public static final String AFTER_STOP_EVENT = "after_stop";
	
	void fire(String event) throws HSFException;
}