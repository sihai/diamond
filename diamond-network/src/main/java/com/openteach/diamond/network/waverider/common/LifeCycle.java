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
package com.galaxy.diamond.network.waverider.common;

/**
 * <p>
 * 生命周期管理接口
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public interface LifeCycle {

	/**
	 * 初始化
	 * @return
	 */
	boolean init();
	
	/**
	 * 启动服务
	 * @return
	 */
	boolean start();
	
	/**
	 * 停止服务
	 * @return
	 */
	boolean stop();
	
	/**
	 * 重启服务
	 * @return
	 */
	boolean restart();
}
