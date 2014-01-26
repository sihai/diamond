/**
 * Copyright 2013 openteach
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
package com.openteach.diamond.network.waverider.slave.failure.slave;

import com.openteach.diamond.network.waverider.common.LifeCycle;
import com.openteach.diamond.network.waverider.config.WaveriderConfig;
import com.openteach.diamond.network.waverider.master.MasterState;

/**
 * Slave端监控Master故障，并做响应的处理
 * 
 * @author raoqiang
 *
 */
public interface MasterFailureMonitor extends LifeCycle {
	
	int 	DEFAULT_STORE_MASTER_STATE_SIZE = 10;
	long 	DEFAULT_FAILURE_MONITOR_INTERVAL = WaveriderConfig.WAVERIDER_DEFAULT_HEART_BEAT_INTERVAL * 4;
	long 	DEFAULT_FAILURE_MONITOR_WAIT_MASTER_STATE_TIME_OUT = WaveriderConfig.WAVERIDER_DEFAULT_HEART_BEAT_INTERVAL * 10;
	
	/**
	 * 
	 * @param masterState
	 */
	void process(MasterState masterState);
}
