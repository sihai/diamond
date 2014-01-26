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
package com.galaxy.diamond.repository.client.impl.database;

import javax.sql.DataSource;

import com.galaxy.diamond.repository.client.Data;
import com.galaxy.diamond.repository.client.Key;
import com.galaxy.diamond.repository.client.RepositoryClient;
import com.galaxy.diamond.repository.client.cache.Cache;
import com.galaxy.diamond.repository.client.cache.FileLRUCacheFactory;
import com.galaxy.diamond.repository.client.cache.HyBridLRUCacheFactory;
import com.galaxy.diamond.repository.client.cache.MemoryLRUCacheFactory;
import com.galaxy.diamond.repository.client.factory.AbstractRepositoryClientFactory;
import com.galaxy.diamond.repository.client.factory.DummyCacheFactory;

/**
 * 
 * @author sihai
 *
 */
public class DatabaseRepositoryClientFactory extends AbstractRepositoryClientFactory {

	@Override
	public RepositoryClient newInstace() {
		DatabaseRepositoryClient client = new DatabaseRepositoryClient(certificate, newCache(), newDataSource());
		client.initialize();
		client.start();
		return client;
	}
	
	/**
	 * 
	 * @return
	 */
	private DataSource newDataSource() {
		DataSourceFactory factory = new DataSourceFactory();
		factory.withCertificate((DatabaseCertificate)certificate);
		return factory.newInstance();
	}
	
	/**
	 * 
	 * @return
	 */
	private Cache<Key, Data> newCache() {
		Cache<Key, Data> cache = null;
		if(cacheType == Cache.Type.DUMMY) {
			cache = new DummyCacheFactory<Key, Data>().newInstance();
		} else if(cacheType == Cache.Type.MEMORY_LRU) {
			cache = new MemoryLRUCacheFactory<Key, Data>().withMaxEntries(maxEntries).withValueConverter(converter).newInstance();
		} else if(cacheType == Cache.Type.FILE_LRU) {
			cache = new FileLRUCacheFactory<Key, Data>().withMaxEntries(maxEntries).withFileName(cacheFileName).withValueConverter(converter).newInstance();
		} else if(cacheType == Cache.Type.HYBRID_LRU) {
			cache = new HyBridLRUCacheFactory<Key, Data>().withMaxEntries(maxEntries).withMaxEntriesInMemory(maxEntriesInMemory).withFileName(cacheFileName).withValueConverter(converter).newInstance();
		} else {
			throw new IllegalArgumentException(String.format("Not supported cache type:%s at now", cacheType));
		}
		return cache;
	}
}