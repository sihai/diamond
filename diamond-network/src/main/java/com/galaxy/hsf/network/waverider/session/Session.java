/**
 * waverider 
 */

package com.galaxy.hsf.network.waverider.session;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.galaxy.hsf.network.waverider.SlaveWorker;
import com.galaxy.hsf.network.waverider.command.Command;
import com.galaxy.hsf.network.waverider.command.exception.ExecuteCommandException;
import com.galaxy.hsf.network.waverider.common.LifeCycle;

/**
 * <p>
 * Session, Master为每一个链接到Master的Slave维持一个Session, Session负责处理对应Slave的所有请求
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public interface Session extends LifeCycle {

	String SESSION_THREAD_NAME_PREFIX = "Waverider-Session";
	
	/**
	 * 
	 * @param command
	 * @throws ExecuteCommandException
	 */
	void execute(Command command) throws ExecuteCommandException;
	
	/**
	 * 释放Session, 以备重用
	 */
	void free();
	
	/**
	 * 转变Session状态
	 */
	void transit();
	
	/**
	 * 激活Session, Master在接收到对应Slave的心跳后将Session激活
	 */
	void alive();

	/**
	 * 判断Session是不是死掉, 对应的Slave死掉
	 * @return
	 */
	boolean isDead();
	
	/**
	 * Session读取网络数据, 对应Slave发送的请求
	 * @throws IOException, InterruptedException
	 */
	public void onRead() throws IOException, InterruptedException;

	/**
	 * Session写网络数据, 发送数据到对应的Slave
	 * @throws IOException
	 */
	void onWrite() throws IOException;
	
	/**
	 * Session对应的网络链接出错
	 */
	void onException(Exception e);
	
	/**
	 * 获取Session对应的Slave信息
	 * @return
	 */
	SlaveWorker getSlaveWorker();
	
	/**
	 * 获取Session的状态
	 * @return
	 */
	SessionStateEnum getState();
	
	/**
	 * 获取Session的通道
	 * @return
	 */
	SocketChannel getChannel();
}
