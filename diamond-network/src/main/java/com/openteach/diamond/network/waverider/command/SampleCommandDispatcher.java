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

import java.util.HashMap;
import java.util.Map;

public class SampleCommandDispatcher implements CommandDispatcher {
	
	/**
	 * 命令路由表
	 */
	private Map<Long, CommandHandler> commandRoutingTable = new HashMap<Long, CommandHandler>();
	
	@Override
	public void addCommandHandler(Long command, CommandHandler handler) {
		commandRoutingTable.put(command, handler);
	}
	
	@Override
	public Command dispatch(Command command) {
		CommandHandler commandHandler = commandRoutingTable.get(command.getType());
		return commandHandler.handle(command);
	}
	
	@Override
	public void setCommandRoutingTable(Map<Long, CommandHandler> commandRoutingTable) {
		this.commandRoutingTable = commandRoutingTable;
	}
}
