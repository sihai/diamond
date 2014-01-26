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

import java.util.List;

/**
 * Command提供者接口，业务代码提供实现该接口的Provider，Master循环取得Command分发到Slave
 * 
 * @author raoqiang
 *
 */
public interface CommandProvider {
	
	/**
	 * 产生Command
	 * @return
	 */
	Command produce();
	
	/**
	 * 批量产生Commnad
	 * 
	 * @param count
	 * @return
	 */
	List<Command> produce(long count);
	
	/**
	 * 
	 * @return
	 */
	String getName();
}
