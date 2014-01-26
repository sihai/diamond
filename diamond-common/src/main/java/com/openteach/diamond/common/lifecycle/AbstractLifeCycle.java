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
package com.openteach.diamond.common.lifecycle;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractLifeCycle implements LifeCycle {

	/**
	 * status
	 */
	private volatile Status status = Status.DESTROYED;
	
	@Override
	public void initialize() {
		if(this.isInitialized()) {
			throw new IllegalStateException("Not destroyed");
		}
		status = Status.INITIALIZED;
	}

	@Override
	public void start() {
		if(!this.isInitialized()) {
			throw new IllegalStateException("Not initialized");
		}
		status = Status.STARTED;
	}

	@Override
	public void stop() {
		if(this.isInitialized()) {
			throw new IllegalStateException("Not started");
		}
		status = Status.STOPPED;
	}

	@Override
	public void destroy() {
		if(this.isInitialized()) {
			throw new IllegalStateException("Not stopped");
		}
		status = Status.DESTROYED;
	}

	@Override
	public boolean isInitialized() {
		return Status.INITIALIZED == status;
	}

	@Override
	public boolean isStarted() {
		return Status.STARTED == status;
	}

	@Override
	public boolean isStopped() {
		return Status.STOPPED == status;
	}

	@Override
	public boolean isDestroyed() {
		return Status.DESTROYED == status;
	}

	@Override
	public void restart() {
		stop();
		initialize();
		start();
	}
}
