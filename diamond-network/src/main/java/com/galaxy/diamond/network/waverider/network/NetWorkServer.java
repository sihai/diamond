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

import com.galaxy.diamond.network.waverider.common.LifeCycle;
import com.galaxy.diamond.network.waverider.session.SessionManager;

/**
 * <p>
 * 网络服务器，运行在Master节点
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public interface NetWorkServer extends LifeCycle, NetWorkEndPoint {
	
	String NET_WORK_SERVER_THREAD_NAME = "Waverider-NetWorkServer-Thread";
	String NET_WORK_READER = "Waverider-NetworkServer-NetworkReader";
	String NET_WORK_WRITER = "Waverider-NetworkServer-NetworkWriter";
	
	int NETWORK_OPERATION_ACCEPT = 0;
	int NETWORK_OPERATION_READ = 1;
	int NETWORK_OPERATION_WRITE = 2;
	
	/**
	 * 
	 * @return
	 */
	String getIp();
	
	/**
	 * 
	 */
	int getPort();
	
	/**
	 * 
	 * @param sessionManager
	 */
	void setSessionManager(SessionManager sessionManager);
}
