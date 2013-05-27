package com.galaxy.hsf.network.waverider.command;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class GreetCommandProvider implements CommandProvider {
	
	private static final String HELLO = "Hello Master !";
	
	private static final int BATCH_SIZE = 100;

	@Override
	public Command produce() {
		return makeGreetCommand();
	}

	@Override
	public List<Command> produce(long count) {
		List<Command> commandList = new LinkedList<Command>();
		for(int i = 0; i < BATCH_SIZE; i++){
			commandList.add(makeGreetCommand());
		}
		
		return commandList;
	}
	
	private Command makeGreetCommand() {
		ByteBuffer buffer = ByteBuffer.allocate(HELLO.getBytes().length);
		buffer.put(HELLO.getBytes());
		buffer.flip();
		
		Command command = new Command(1L, buffer);
		
		return command;
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName();
	}

}
