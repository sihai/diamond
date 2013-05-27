package com.galaxy.hsf.network.waverider.command;

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
