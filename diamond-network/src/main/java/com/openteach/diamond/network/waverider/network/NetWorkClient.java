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
package com.openteach.diamond.network.waverider.network;

import java.io.IOException;

import com.openteach.diamond.network.waverider.command.Command;

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
