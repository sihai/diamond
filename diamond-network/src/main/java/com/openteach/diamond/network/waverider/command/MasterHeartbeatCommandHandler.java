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
package com.openteach.diamond.network.waverider.command;

import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.network.waverider.Node;
import com.openteach.diamond.network.waverider.master.MasterState;
import com.openteach.diamond.network.waverider.slave.SlaveState;

/**
 * <p>
 * Master端处理Heartbeat Command处理器
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public class MasterHeartbeatCommandHandler implements CommandHandler {
	
	private static final Log logger = LogFactory.getLog(MasterHeartbeatCommandHandler.class);
	
	private Node master;
	
	public MasterHeartbeatCommandHandler(Node master) {
		this.master = master;
	}

	@Override
	public Command handle(Command command) {
		//logger.info(new StringBuilder("Master receive heartbeat from slave : ").append(command.getSession().getSlaveWorker().getIp()));
		command.getSession().alive();
		master.acceptStatistics(SlaveState.fromByteBuffer(command.getPayLoad()));
		ByteBuffer buffer = ((MasterState)master.gatherStatistics()).toByteBuffer();
		//logger.info("MasterState to bytebuffer size: " + buffer.limit());
		return CommandFactory.createHeartbeatCommand(buffer);
	}
}
