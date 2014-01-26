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

import junit.framework.TestCase;

import org.junit.Test;

import com.galaxy.diamond.repository.client.Certificate;
import com.galaxy.diamond.repository.client.Data;
import com.galaxy.diamond.repository.client.Key;
import com.galaxy.diamond.repository.client.RepositoryClient;
import com.galaxy.diamond.repository.client.cache.Cache;
import com.galaxy.diamond.repository.client.impl.database.DatabaseCertificate;
import com.galaxy.diamond.repository.client.impl.database.DatabaseRepositoryClientFactory;

/**
 * 
 * @author sihai
 *
 */
public class DatabaseRepositoryClientTest extends TestCase {

	@Test
	public void test() throws Exception {
		Certificate certificate = makeDatabaseCertificate();
		RepositoryClient client = new DatabaseRepositoryClientFactory().withCertificate(certificate)
				.enableCache().withCacheType(Cache.Type.HYBRID_LRU).withMaxEntries(Cache.DEFAULT_MAX_ENTRIES)
				.withMaxEntriesInMemory(Cache.DEFAULT_MAX_ENTRIES / 5).withCacheFileName("/tmp/hsf.repostory.client.cache").newInstace();
		for(int i = 0; i < 10000; i++) {
			client.put(client.newData("key" + i, "value" + i, 0L));
		}
		
		for(int i = 0; i < 10000; i++) {
			Key key = client.newKey("key" + i);
			Data data = client.get(key);
			if(!data.getKey().equals(key)) {
				throw new RuntimeException("Wrong");
			}
			if(!data.getValue().equals("value" + i)) {
				throw new RuntimeException("Wrong");
			}
		}
		
		client.stop();
		client.destroy();
	}

	/**
	 * 
	 * @return
	 */
	private Certificate makeDatabaseCertificate() {
		DatabaseCertificate certificate = new DatabaseCertificate();
		certificate.setAppName("testApp");
		certificate.setSecret("123456");
		certificate.setNamespace("testApp");
		certificate.setDriverClassName("com.mysql.jdbc.Driver");
		certificate.setUrl("jdbc:mysql://localhost:3306/repository");
		certificate.setUsername("repository");
		certificate.setPassword("repository");
		return certificate;
	}
}
