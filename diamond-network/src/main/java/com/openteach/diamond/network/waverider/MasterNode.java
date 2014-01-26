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
package com.openteach.diamond.network.waverider;

import com.openteach.diamond.network.waverider.master.SlaveListenter;


/**
 * <p>
 * 基于Master-Slave结构分布式命令执行框架Master节点接口
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public interface MasterNode extends Node {

	/**
	 * 注册SlaveListenter, 覆盖原有的注册
	 * @param listener
	 * 		null	 	取消注册
	 * 		非null		覆盖原有注册
	 */
	void registerSlaveListener(SlaveListenter listener);
}
