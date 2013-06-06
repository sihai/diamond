/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.repository.client;

import java.io.Serializable;

/**
 * 
 * @author sihai
 *
 */
public class Data implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2326678071487608634L;

	/**
	 * 
	 */
	private Key	   key;
	
	/**
	 * 
	 */
	private Serializable value;

	public Data(Key key, Serializable value, long sequence) {
		this.key = key;
		this.value = value;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Serializable getValue() {
		return value;
	}

	public void setValue(Serializable value) {
		this.value = value;
	}
}