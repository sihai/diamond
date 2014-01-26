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
package com.openteach.diamond.service.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.openteach.diamond.common.ThreadSafe;

/**
 * 
 * @author sihai
 *
 */
@ThreadSafe
public class WeightRuleResultCache {

	private Map<String, WeightRule> weightRuleCache;
	
	/**
     *                   
     * 服务名------
     *            --weight--urls
     */
	private Map<String, List<List<String>>> weightRuleResultCache = new HashMap<String, List<List<String>>>();
	
	/**
	 * protected weightRuleCache
	 */
	private final ReentrantReadWriteLock _rw_lock_ = new ReentrantReadWriteLock();

	/**
	 * 
	 * @param serviceUniqueName
	 * @param weightAndUrls
	 */
    public void putWeightRuleResult(String serviceUniqueName, List<List<String>> weightAndUrls) {
    	try {
    		_rw_lock_.writeLock().lock();
    		weightRuleResultCache.put(serviceUniqueName, weightAndUrls);
    	} finally {
    		_rw_lock_.writeLock().unlock();
    	}
    }
    
    /**
     * 
     * @param serviceUniqueName
     * @param weight
     * @return
     */
    public List<String> getServiceURLs(String serviceUniqueName, Integer weight) {
    	try {
    		_rw_lock_.readLock().lock();
	        List<List<String>> weightList = weightRuleResultCache.get(serviceUniqueName);
	        if(weightList != null) {
	        	// XXX copy one
	            return new ArrayList<String>(weightList.get(weight));
	        } else {
	            return null;
	        }
    	} finally {
    		_rw_lock_.readLock().unlock();
    	}
    }

    /**
     * 
     * @param serviceUniqueName
     * @return
     */
    public List<List<String>> getWeightRuleCacheList(String serviceUniqueName) {
    	try {
    		_rw_lock_.readLock().lock();
    		
    		// XXX copy one , there not depth copy, there is no problem, please see putWeightRule method update model
    		return new ArrayList<List<String>>(weightRuleResultCache.get(serviceUniqueName));
    	} finally {
    		_rw_lock_.readLock().unlock();
    	}
    }
}
