/**
 * Copyright 2013 Qiangqiang RAO
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
package com.galaxy.diamond.util;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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
		int port = s.getLocalPort();
		s.close();
		return port;
	}
	
	/**
	 * 
	 * @return
	 * @throws SocketException
	 */
	public static String getLocalIp() throws SocketException {
		List<String> ipList = getLocalIps();
		if(ipList.isEmpty()) {
			throw new SocketException("No ip found");
		}
		
		return ipList.get(0);
	}
	
	/**
	 * 
	 * @return
	 * @throws SocketException 
	 */
	public static List<String> getLocalIps() throws SocketException {
		List<String> ipList = new ArrayList<String>(2);
		NetworkInterface ni = null;
		Enumeration<InetAddress> addresses = null;
		InetAddress address = null;
		String ip = null;
		Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
		while(enumeration.hasMoreElements()) {
			ni = enumeration.nextElement();
			if(ni.getName().startsWith("eth") || ni.getName().startsWith("wlan")) {
				addresses = ni.getInetAddresses();
				while(addresses.hasMoreElements()) {
					address = addresses.nextElement();
					if (address instanceof Inet4Address) {
						ip = address.getHostAddress();
						if(!StringUtils.equals("127.0.0.1", ip)) {
							ipList.add(ip);
						}
					}
				}
			}
		}
		return ipList;
	}
}
