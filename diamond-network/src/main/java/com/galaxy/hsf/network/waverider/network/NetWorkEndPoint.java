/**
 * waverider
 * 
 */

package com.galaxy.hsf.network.waverider.network;

import java.nio.channels.SocketChannel;

import com.galaxy.hsf.network.waverider.common.LifeCycle;

/**
 * <p>
 * 网络对等节点
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public interface NetWorkEndPoint extends LifeCycle {

	/**
	 * 通知有数据要写
	 * @param channel
	 * @return 
	 */
	boolean notifyWrite(SocketChannel channel);
	
	/**
	 * 通知读数据请求
	 * @param channel
	 * @return 
	 */
	boolean notifyRead(SocketChannel channel);
}
