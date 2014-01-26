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
package com.galaxy.diamond.network.waverider.session;

import java.nio.channels.SocketChannel;
import java.util.List;

import com.galaxy.diamond.network.waverider.command.CommandDispatcher;
import com.galaxy.diamond.network.waverider.common.LifeCycle;
import com.galaxy.diamond.network.waverider.network.NetWorkServer;

/**
 * <p>
 * Master端Session管理器
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public interface SessionManager extends LifeCycle {
	
	String SESSION_RECYCLE_THREAD_NAME_PREFIX = "Waverider-Sesion-Recycle";
	
	/**
	 * 设置Session的命令分发器
	 * @param commandDispatcher
	 */
	void setCommandDispatcher(CommandDispatcher commandDispatcher);
	
	/**
	 * 分配session
	 * @param netWorkServer
	 * @param channel
	 * @param start
	 * @return
	 */
	Session newSession(NetWorkServer netWorkServer, SocketChannel channel, boolean start);
	
	/**
	 * 释放session
	 * @param session
	 */
	void freeSession(Session session);
	
	/**
	 * 收集Session状态
	 * @return
	 */
	List<SessionState> generateSessionState();
	
	/**
	 * 注册Session监听器
	 * @param listener
	 *  	null	 	取消注册
	 * 		非null		覆盖原有注册
	 */
	void registerSessionListener(SessionListener listener);
}
