/**
 * waverider
 *  
 */

package com.galaxy.hsf.network.waverider.network;

import java.io.IOException;

import com.galaxy.hsf.network.waverider.command.Command;

/**
 * <p>
 * 网络客户端，运行在Slave节点
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public interface NetWorkClient extends NetWorkEndPoint {
	
	String NET_WORK_CLIENT_THREAD_NAME = "Waverider-NetWorkClient-Thread";
	
	/**
	 * 发送命令
	 * @param command
	 * @throws InterruptedException
	 */
	void send(Command command) throws InterruptedException;
	
	/**
	 * 接收命令, 方法可能阻塞直到解析到命令
	 * @return
	 * @throws IOException, InterruptedException
	 */
	Command receive() throws IOException, InterruptedException;
}
