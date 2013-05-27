/**
 * waverider 
 */

package com.galaxy.hsf.network.waverider.session;

/**
 * <p>
 * Session 监听器, 监听Session的创建, 销毁
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public interface SessionListener {
	
	/**
	 * 分配了新的Session
	 * @param session
	 */
	void sessionAllocated(Session session);
	
	/**
	 * 释放了Session
	 * @param session
	 */
	void sessionReleased(Session session);
}
