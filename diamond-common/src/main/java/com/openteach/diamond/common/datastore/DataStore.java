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
package com.galaxy.diamond.common.datastore;

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