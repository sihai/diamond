/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.address;

import org.apache.commons.lang.StringUtils;

/**
 * Supported service protocols
 * 
 * @author sihai
 *
 */
public enum Protocol {

	HSF("hsf"),
	HTTP("http"),
	HTTPS("https"),
	XFIRE("xfire");
	
	private final String name;
	
	/**
	 * 
	 * @param name
	 */
	private Protocol(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @return
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * 
	 * @param protocol
	 * @return
	 */
	public static final Protocol toEnum(String protocol) {
		for(Protocol p : Protocol.values()) {
			if(StringUtils.equals(p.name, protocol)) {
				return p;
			}
		}
		return null;
	}
}
