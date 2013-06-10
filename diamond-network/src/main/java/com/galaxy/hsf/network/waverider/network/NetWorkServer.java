/**
 * waverider
 * 
 */
package com.galaxy.hsf.network.waverider.network;

import com.galaxy.hsf.network.waverider.common.LifeCycle;
import com.galaxy.hsf.network.waverider.session.SessionManager;

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
