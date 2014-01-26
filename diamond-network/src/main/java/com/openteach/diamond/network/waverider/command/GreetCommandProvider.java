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
