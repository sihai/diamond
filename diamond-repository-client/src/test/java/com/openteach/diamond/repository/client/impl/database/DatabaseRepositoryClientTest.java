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

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.openteach.diamond.repository.client.Certificate;
import com.openteach.diamond.repository.client.Data;
import com.openteach.diamond.repository.client.Key;
import com.openteach.diamond.repository.client.RepositoryClient;
import com.openteach.diamond.repository.client.cache.Cache;

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
				.withMaxEntriesInMemory(Cache.DEFAULT_MAX_ENTRIES / 5).withCacheFileName("/tmp/diamond.repostory.client.cache").newInstace();
		for(int i = 0; i < 10000; i++) {
			client.put(client.newData("key_" + i, "value_" + i));
		}
		
		for(int i = 0; i < 10000; i++) {
			Key key = client.newKey("key_" + i);
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
	
	@Test
	public void test_sub_key() throws Exception {
		Certificate certificate = makeDatabaseCertificate();
		RepositoryClient client = new DatabaseRepositoryClientFactory().withCertificate(certificate)
				.enableCache().withCacheType(Cache.Type.HYBRID_LRU).withMaxEntries(Cache.DEFAULT_MAX_ENTRIES)
				.withMaxEntriesInMemory(Cache.DEFAULT_MAX_ENTRIES / 5).withCacheFileName("/tmp/diamond.repostory.client.cache").newInstace();
		for(int i = 0; i < 100; i++) {
			for(int j = 0; j < 10; j++) {
				client.put(client.newData("key_" + i, "sub_key_" + j, String.format("value_%d_%d_", i, j)));
			}
		}
		
		for(int i = 0; i < 100; i++) {
			
			Key key = client.newKey("key_" + i);
			List<Data> dList = client.mget(key);
			if(10 != dList.size()) {
				throw new RuntimeException("Wrong");
			}
			for(int j = 0; j < 10; j++) {
				if(!dList.get(j).getValue().equals(String.format("value_%d_%d_", i, j))) {
					throw new RuntimeException("Wrong");
				}
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
