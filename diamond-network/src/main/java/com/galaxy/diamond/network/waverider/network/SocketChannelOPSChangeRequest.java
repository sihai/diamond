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
 */
package com.galaxy.diamond.network.waverider.network;

import java.nio.channels.SocketChannel;

/**
 * <p>
 * SocketChannel 更新请求
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */

public class SocketChannelOPSChangeRequest {

	private SocketChannel channel;
	private int ops;
	  
	public SocketChannel getChannel() {
		return channel;
	}

	public synchronized int getOps() {
		return ops;
	}
	
	public synchronized void addOps(int ops) {
		this.ops |= ops;
	}
	public synchronized void clearOps(int ops) {
		this.ops &= ~ops;
	}
	
	public SocketChannelOPSChangeRequest(SocketChannel channel, int ops) {
		this.channel = channel;
		this.ops = ops;
	}
}
