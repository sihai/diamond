/**
 * Copyright 2013 openteach
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 */
package com.openteach.diamond.util;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 
 * @author sihai
 *
 */
public class NetworkUtil {

	public static final String LOCAL_IP;
	
	static {
		try {
			List<String> ips = getLocalIps();
			if(ips.isEmpty()) {
				throw new RuntimeException("No ip found");
			}
			LOCAL_IP = ips.get(0);
		} catch (SocketException e) {
			throw new RuntimeException("Can not get local ip", e);
		}
	}
	/**
	 * 
	 * @return
	 * @throws IOException 
	 */
	public static int getFreePort() throws IOException {
		ServerSocket s = new ServerSocket(0);
		int port = s.getLocalPort();
		s.close();
		return port;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getLocalIp() {
		return LOCAL_IP;
	}
	
	/**
	 * 
	 * @return
	 * @throws SocketException 
	 */
	private static List<String> getLocalIps() throws SocketException {
		List<String> ipList = new ArrayList<String>(2);
		NetworkInterface ni = null;
		Enumeration<InetAddress> addresses = null;
		InetAddress address = null;
		Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
		while(enumeration.hasMoreElements()) {
			ni = enumeration.nextElement();
			if (ni.isLoopback() || ni.isVirtual() || !ni.isUp()) {
                continue;
			}
			addresses = ni.getInetAddresses();
			while(addresses.hasMoreElements()) {
				address = addresses.nextElement();
				if (address instanceof Inet4Address) {
					ipList.add(address.getHostAddress());
				}
			}
		}
		return ipList;
	}
}
