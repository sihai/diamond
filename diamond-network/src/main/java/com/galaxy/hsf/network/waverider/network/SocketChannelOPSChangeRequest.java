/**
 * waverider 
 */


package com.galaxy.hsf.network.waverider.network;

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
