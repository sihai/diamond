package com.galaxy.hsf.network.waverider.slave.failure.slave;

import java.util.List;

import com.galaxy.hsf.network.waverider.master.MasterState;

/**
 * Slave端监控Master故障，并做响应的处理
 * 
 * @author raoqiang
 *
 */
public interface MasterFailureHandler {
	
	/**
	 * 
	 * @param masterStateList
	 */
	void handle(List<MasterState> masterStateList);
}
