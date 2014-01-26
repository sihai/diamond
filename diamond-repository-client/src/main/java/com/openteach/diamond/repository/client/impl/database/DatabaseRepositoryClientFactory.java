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
 * 
 */
package com.openteach.diamond.repository.client.impl.database;

import javax.sql.DataSource;

import com.openteach.diamond.repository.client.Key;
import com.openteach.diamond.repository.client.RepositoryClient;
import com.openteach.diamond.repository.client.cache.Cache;
import com.openteach.diamond.repository.client.cache.FileLRUCacheFactory;
import com.openteach.diamond.repository.client.cache.HyBridLRUCacheFactory;
import com.openteach.diamond.repository.client.cache.MemoryLRUCacheFactory;
import com.openteach.diamond.repository.client.factory.AbstractRepositoryClientFactory;
import com.openteach.diamond.repository.client.factory.DummyCacheFactory;

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
	private Cache<Key, Object> newCache() {
		Cache<Key, Object> cache = null;
		if(cacheType == Cache.Type.DUMMY) {
			cache = new DummyCacheFactory<Key, Object>().newInstance();
		} else if(cacheType == Cache.Type.MEMORY_LRU) {
			cache = new MemoryLRUCacheFactory<Key, Object>().withMaxEntries(maxEntries).withValueConverter(converter).newInstance();
		} else if(cacheType == Cache.Type.FILE_LRU) {
			cache = new FileLRUCacheFactory<Key, Object>().withMaxEntries(maxEntries).withFileName(cacheFileName).withValueConverter(converter).newInstance();
		} else if(cacheType == Cache.Type.HYBRID_LRU) {
			cache = new HyBridLRUCacheFactory<Key, Object>().withMaxEntries(maxEntries).withMaxEntriesInMemory(maxEntriesInMemory).withFileName(cacheFileName).withValueConverter(converter).newInstance();
		} else {
			throw new IllegalArgumentException(String.format("Not supported cache type:%s at now", cacheType));
		}
		return cache;
	}
}
