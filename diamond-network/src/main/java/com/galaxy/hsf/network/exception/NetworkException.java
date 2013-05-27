/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network.exception;

/**
 * 
 * @author sihai
 *
 */
public class NetworkException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -580798176970752200L;

	public NetworkException(String msg) {
		this(msg, null);
	}
	
	public NetworkException(Throwable t) {
		this(null, t);
	}
	
	public NetworkException(String msg, Throwable t) {
		super(msg, t);
	}
	
}
