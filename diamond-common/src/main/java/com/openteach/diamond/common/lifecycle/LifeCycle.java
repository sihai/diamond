/**
 * Copyright 2013 openteach
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
