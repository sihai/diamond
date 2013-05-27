/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.route;

import com.galaxy.hsf.common.exception.HSFException;

/**
 * 
 * @author sihai
 *
 */
public class RouteRuleParserException extends HSFException {

	public RouteRuleParserException() {
		super();
	}

	public RouteRuleParserException(String message) {
		super(message);
	}

	public RouteRuleParserException(String message, Throwable cause) {
		super(message, cause);
	}
}
