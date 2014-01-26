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
package com.openteach.diamond.network.waverider;

import com.openteach.diamond.network.waverider.command.CommandHandler;
import com.openteach.diamond.network.waverider.common.LifeCycle;

/**
 * <p>
 * 分布式节点，Master节点MasterNodel和Slave节点SlaveNode实现本接口
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public interface Node extends LifeCycle {
	
	/**
	 * 添加命令处理器, command=0,系统保留给Heartbeat command
	 * @param command
	 * @param handler
	 */
	void addCommandHandler(Long command, CommandHandler handler);
	
	/**
	 * 收集统计信息
	 * 
	 * @return
	 */
	State gatherStatistics();
	
	/**
	 * 
	 * @param state
	 */
	void acceptStatistics(State state);

}
