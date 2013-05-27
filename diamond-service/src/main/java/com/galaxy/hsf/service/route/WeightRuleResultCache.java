/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.galaxy.hsf.common.ThreadSafe;

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
