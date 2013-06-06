/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.router;

import java.util.List;

import com.galaxy.hsf.common.lifecycle.LifeCycle;

/**
 * 
 * @author sihai
 *
 */
public interface ServiceRouter extends LifeCycle {
	
	/**
	 * 
	 * @param parameter
	 * @return
	 */
	List<String> route(RouteParameter parameter);
}
