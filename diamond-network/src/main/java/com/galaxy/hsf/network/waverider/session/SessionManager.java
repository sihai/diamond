/**
 * waverider
 * 
 */

package com.galaxy.hsf.network.waverider.session;

import java.nio.channels.SocketChannel;
import java.util.List;

import com.galaxy.hsf.network.waverider.command.CommandDispatcher;
import com.galaxy.hsf.network.waverider.common.LifeCycle;
import com.galaxy.hsf.network.waverider.network.NetWorkServer;

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
