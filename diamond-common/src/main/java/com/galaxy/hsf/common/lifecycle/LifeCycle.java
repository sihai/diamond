/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.common.lifecycle;

/**
 * Life cycle management interface
 * @author sihai
 *
 */
public interface LifeCycle {
	
	/**
	 * initialize
	 */
	void initialize();
	
	/**
	 * start
	 */
	void start();
	
	/**
	 * stop
	 */
	void stop();
	
	/**
	 * restart
	 */
	void restart();
	
	/**
	 * release resources
	 */
	void destroy();
	
	/**
	 * Returns true if the resource state is initialized.
	 * @return
	 */
	boolean isInitialized();
	
	/**
     * Returns true if the resource state is started.
     * @return.
     */
    boolean isStarted();

    /**
     * Returns true if the resource state is stopped.
     * @return
     */
    boolean isStopped();

    /**
     * Returns true if the resource state is destroyed.
     * @return
     */
    boolean isDestroyed();
    
    /**
     * 
     * @author sihai
     *
     */
    enum Status {
    	/**
    	 * Initialized status.
    	 */
    	INITIALIZED,
    	
	    /**
	     * Started status.
	     */
	    STARTED,

	    /**
	     * Stopped status.
	     */
	    STOPPED,

	    /**
	     * Destroyed status.
	     */
	    DESTROYED,

	    /**
	     * Failed status, equivalent to STOPPED state and reserved for a failed recovery of statement state; Not applicable to core engine.
	     */
	    FAILED
	}
}
