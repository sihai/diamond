/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.common.resource;

/**
 * 
 * @author sihai
 *
 */
public class ResourceConfig {

	public static final int DEFAULT_MIN_THREAD_COUNT = 4;
	
	public static final int DEFAULT_MAX_THREAD_COUNT = 512;
	
	public static final int DEFAULT_KEEP_ALIVE_TIME = 60;
	
	public static final int DEFAULT_QUEUE_SIZE = 4;
	
	/**
	 * 
	 */
	private int minThreadCount = DEFAULT_MIN_THREAD_COUNT;
	
	/**
	 * 
	 */
	private int maxThreadCount = DEFAULT_MAX_THREAD_COUNT;
	
	/*
	 * 
	 */
	private int keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;
	
	/**
	 * 
	 */
	private int queueSize = DEFAULT_QUEUE_SIZE;

	public int getMinThreadCount() {
		return minThreadCount;
	}

	public void setMinThreadCount(int minThreadCount) {
		this.minThreadCount = minThreadCount;
	}

	public int getMaxThreadCount() {
		return maxThreadCount;
	}

	public void setMaxThreadCount(int maxThreadCount) {
		this.maxThreadCount = maxThreadCount;
	}

	public int getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(int keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}
}
