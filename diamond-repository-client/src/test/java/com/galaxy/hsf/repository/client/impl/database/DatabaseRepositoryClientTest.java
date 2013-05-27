/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.repository.client.impl.database;

import junit.framework.TestCase;

import org.junit.Test;

import com.galaxy.hsf.repository.client.Certificate;
import com.galaxy.hsf.repository.client.Data;
import com.galaxy.hsf.repository.client.Key;
import com.galaxy.hsf.repository.client.RepositoryClient;
import com.galaxy.hsf.repository.client.cache.Cache;

/**
 * 
 * @author sihai
 *
 */
public class DatabaseRepositoryClientTest extends TestCase {

	@Test
	public void test() {
		Certificate certificate = makeDatabaseCertificate();
		RepositoryClient client = new DatabaseRepositoryClientFactory().withCertificate(certificate)
				.enableCache().withCacheType(Cache.Type.HYBRID_LRU).withMaxEntries(Cache.DEFAULT_MAX_ENTRIES)
				.withMaxEntriesInMemory(Cache.DEFAULT_MAX_ENTRIES / 5).withCacheFileName("/tmp/hsf.repostory.client.cache").newInstace();
		for(int i = 0; i < 10000; i++) {
			client.put(client.newData("key" + i, "value" + i));
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
