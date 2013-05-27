/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.router;

import com.galaxy.hsf.address.Protocol;

/**
 * 
 * @author sihai
 *
 */
public class RouteParameter {

	/**
	 * 
	 */
	private Protocol protocol;
	
	/**
	 * 
	 */
	private String serviceName;
	
	/**
	 * 
	 */
	private String method;
	
	/**
	 * 
	 */
	private String[] parameterTypes;
	
	/**
	 * 
	 */
	private Object[] args;

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(String[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}
}
