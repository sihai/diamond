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
package com.openteach.diamond.network.waverider.slave.failure.slave;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.network.waverider.SlaveNode;
import com.openteach.diamond.network.waverider.master.MasterState;

/**
 * 
 * @author sihai
 *
 */
public class DefaultMasterFailureHandler implements MasterFailureHandler {

	private static final Log logger = LogFactory.getLog(DefaultMasterFailureHandler.class);
	
	private SlaveNode slave;
	
	public DefaultMasterFailureHandler(SlaveNode slave) {
		this.slave = slave;
	}
	
	@Override
	public void handle(List<MasterState> masterStateList) {
		logger.warn("Master down");
		dump(masterStateList);
	}
	
	private void dump(List<MasterState> masterStateList) {
		logger.debug("Start to dump masterStateList in MasterFailureMonitor:");
		for(MasterState masterState : masterStateList) {
			logger.debug(masterState);
		}
	}

}
