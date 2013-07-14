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
package com.galaxy.diamond.network.waverider.command;

import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.diamond.network.waverider.session.Session;

/**
 * 
 * @author raoqiang
 *
 */
public class MasterGreetCommandHandler implements CommandHandler {
	
	private static final Log logger = LogFactory.getLog(MasterGreetCommandHandler.class);
	
	@Override
	public Command handle(Command command) {
		logger.info(new StringBuilder("Command type = ").append(command.getType()).toString());
		String hello = new String(command.getPayLoad().array());
		logger.info(new StringBuilder("Master receive greet from slave : ").append(command.getSession().getSlaveWorker().getIp()).append(", say:").append(hello));
		command.getSession().alive();
		return makeGreetCommand(command.getSession());
	}
	
	private Command makeGreetCommand(Session session) {
		String hello = new StringBuilder("Hello slave:").append(session.getSlaveWorker().getIp()).toString();
		ByteBuffer buffer = ByteBuffer.allocate(hello.getBytes().length);
		buffer.put(hello.getBytes());
		buffer.flip();
		
		Command command = new Command(1L, buffer);
		
		return command;
	}

}
