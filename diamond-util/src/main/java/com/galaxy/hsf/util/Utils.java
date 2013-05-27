/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author sihai
 *
 */
public class Utils {
	
	private static final Log logger = LogFactory.getLog(Utils.class);
	
	public static final String HOST_NAME;
	
	static {
		try {
			HOST_NAME = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        	throw new RuntimeException("OOPS: Can not fetch host name", e);
        }
	}
}
