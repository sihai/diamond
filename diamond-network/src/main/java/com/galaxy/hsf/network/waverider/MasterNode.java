/**
 * waverider
 *  
 */

package com.galaxy.hsf.network.waverider;

import com.galaxy.hsf.network.waverider.master.SlaveListenter;


/**
 * <p>
 * 基于Master-Slave结构分布式命令执行框架Master节点接口
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public interface MasterNode extends Node {

	/**
	 * 注册SlaveListenter, 覆盖原有的注册
	 * @param listener
	 * 		null	 	取消注册
	 * 		非null		覆盖原有注册
	 */
	void registerSlaveListener(SlaveListenter listener);
}
