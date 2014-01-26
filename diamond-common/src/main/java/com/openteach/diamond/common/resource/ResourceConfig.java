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
package com.galaxy.diamond.common.resource;

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
