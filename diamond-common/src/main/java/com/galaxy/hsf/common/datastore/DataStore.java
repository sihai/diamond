/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.common.datastore;

/**
 * 
 * @author sihai
 *
 */
public interface DataStore {
	
	/**
     * 放入需要存储的OSGi服务的数据
     *
     * @param componentName
     * @param key
     * @param value
     */
    void put(String componentName, String key, Object value);

    /**
     * 获取存储的OSGi服务的数据
     *
     * @param componentName
     * @param key
     * @return Object
     */
    <T> T get(String componentName, String key);

    /**
     * 删除存储的OSGi服务的数据
     *
     * @param componentName
     * @param key
     */
    void remove(String componentName, String key);
}