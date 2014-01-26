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
 */
package com.openteach.diamond.address;

import org.apache.commons.lang.StringUtils;

/**
 * Supported service protocols
 * 
 * @author sihai
 *
 */
public enum Protocol {

	DIAMOND("diamond"),
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
