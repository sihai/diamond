/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.container.third.tomcat;

import com.galaxy.hsf.container.HSFContainer;

/**
 * 描述：在tomcat中启动HSF容器
 * @author sihai
 *
 */
public class HSFContainerDelegator {

	/**
	 * 启动方式：
	 * 1、直接启动HSF容器，classloader由HSFAppClassLoader类去完成
	 */
	public void start(String hsfcontainerPath) throws Exception{
		String[] args = new String[]{ hsfcontainerPath };
		HSFContainer.start(args);
	}
}
