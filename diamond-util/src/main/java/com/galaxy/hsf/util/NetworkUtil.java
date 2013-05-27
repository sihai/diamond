/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/**
 * 
 * @author sihai
 *
 */
public class NetworkUtil {

	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	public static int getFreePort() throws IOException {
		ServerSocket s = new ServerSocket(0);
		return s.getLocalPort();
	}
	
	/**
	 * 
	 * @return
	 * @throws UnknownHostException 
	 */
	public static String getLocalIp() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}
}
