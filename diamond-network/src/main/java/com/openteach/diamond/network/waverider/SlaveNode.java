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

import com.openteach.diamond.network.exception.NetworkException;
import com.openteach.diamond.network.waverider.command.Command;
import com.openteach.diamond.network.waverider.slave.MasterDeadCallback;

/**
 * <p>
 * 基于Master-Slave结构分布式命令执行框架Slave节点接口
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public interface SlaveNode extends Node {

	String SLAVE_COMMAND_PROVIDER_THREAD_NAME = "Waverider-Slave-Command-Provider-Thread";
	String SLAVE_HEART_BEAT_THREAD_NAME_PREFIX = "Waverider-Slave-Heartbeat";
	String SLAVE_COMMAND_DISPATCHE_THREAD_NAME = "Waverider-Slave-Command-Dispatcher-Thread";
	
	/**
	 * 
	 * @param command
	 * @throws NetworkException
	 */
	void execute(Command command) throws NetworkException;
	
	/**
	 * 注册MasterDeadCallback回调, 覆盖原有的注册
	 * @param callback
	 * 		null	 	取消注册
	 * 		非null		覆盖原有注册
	 */
	void registerMasterDeadCallback(MasterDeadCallback callback);
}
