/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.common.lifecycle;

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
		if(this.isInitialized()) {
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
		return Status.INITIALIZED == status || Status.STARTED == status;
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
