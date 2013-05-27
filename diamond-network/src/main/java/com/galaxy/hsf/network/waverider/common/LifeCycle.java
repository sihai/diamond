/**
 * waverider
 * 
 */

package com.galaxy.hsf.network.waverider.common;

/**
 * <p>
 * 生命周期管理接口
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public interface LifeCycle {

	/**
	 * 初始化
	 * @return
	 */
	boolean init();
	
	/**
	 * 启动服务
	 * @return
	 */
	boolean start();
	
	/**
	 * 停止服务
	 * @return
	 */
	boolean stop();
	
	/**
	 * 重启服务
	 * @return
	 */
	boolean restart();
}
