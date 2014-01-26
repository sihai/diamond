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
 * 
 */
package com.openteach.diamond.container.third.tomcat;

import com.openteach.diamond.container.Container;

/**
 * 描述：在tomcat中启动HSF容器
 * @author sihai
 *
 */
public class ContainerDelegator {

	/**
	 * 启动方式：
	 * 1、直接启动HSF容器，classloader由HSFAppClassLoader类去完成
	 */
	public void start(String hsfcontainerPath) throws Exception{
		String[] args = new String[]{ hsfcontainerPath };
		Container.start(args);
	}
}
