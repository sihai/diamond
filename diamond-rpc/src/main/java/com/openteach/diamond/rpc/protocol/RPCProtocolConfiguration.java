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
package com.openteach.diamond.rpc.protocol;

/**
 * 
 * @author sihai
 *
 */
public class RPCProtocolConfiguration {

	public static final int DEFAULT_MAX_SESSION_PRE_HOST = 32;
	
	/**
	 * 
	 */
	private int maxSessionPreHost = DEFAULT_MAX_SESSION_PRE_HOST;

	public int getMaxSessionPreHost() {
		return maxSessionPreHost;
	}

	public void setMaxSessionPreHost(int maxSessionPreHost) {
		this.maxSessionPreHost = maxSessionPreHost;
	}
}
