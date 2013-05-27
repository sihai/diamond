/**
 * waverider
 *  
 */
package com.galaxy.hsf.network.waverider.command.exception;

/**
 * 
 * @author sihai
 *
 */
public class ExecuteCommandException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2083413527213333469L;

	public ExecuteCommandException(String msg) {
		this(msg, null);
	}
	
	public ExecuteCommandException(Throwable t) {
		this(null, t);
	}
	
	public ExecuteCommandException(String msg, Throwable t) {
		super(msg, t);
	}
}
