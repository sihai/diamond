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
package com.openteach.diamond.network.waverider.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.network.waverider.Node;
import com.openteach.diamond.network.waverider.master.MasterState;

/**
 * <p>
 * Slave端处理Heartbeat Command处理器
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public class SlaveHeartbeatCommandHandler implements CommandHandler {
	
	private static final Log logger = LogFactory.getLog(SlaveHeartbeatCommandHandler.class);
	
	private Node slave;
	
	public SlaveHeartbeatCommandHandler(Node slave) {
		this.slave = slave;
	}
	
	@Override
	public Command handle(Command command) {
		//logger.info("Slave received heartbeat from master");
		slave.acceptStatistics(MasterState.fromByteBuffer(command.getPayLoad()));
		return null;
		//return CommandFactory.createHeartbeatCommand(((SlaveState)slave.gatherStatistics()).toByteBuffer());
	}
}
