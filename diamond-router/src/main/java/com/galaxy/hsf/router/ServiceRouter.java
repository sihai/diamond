/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.router;

import java.util.List;

/**
 * 
 * @author sihai
 *
 */
public interface ServiceRouter {
	
	/**
	 * 
	 * @param parameter
	 * @return
	 */
	List<String> route(RouteParameter parameter);
}
