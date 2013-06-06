/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.repository.client.factory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.galaxy.diamond.repository.client.Certificate;
import com.galaxy.diamond.repository.client.Data;
import com.galaxy.diamond.repository.client.cache.Cache;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractRepositoryClientFactory implements
		RepositoryClientFactory {

	/**
	 * Certificate for connect to server
	 */
	protected Certificate certificate;
	
	/**
	 * 
	 */
	protected boolean isEnableCache = true;
	
	/**
	 * 
	 */
	protected Cache.ValueConverter<Data> converter = new Cache.ValueConverter<Data>() {

		@Override
		public byte[] toBytes(Data v) {
			ByteArrayOutputStream boutput = null;
			ObjectOutputStream ooutput = null;
			try {
				boutput = new ByteArrayOutputStream();
				ooutput = new ObjectOutputStream(boutput);
				ooutput.writeObject(v);
				ooutput.flush();
				return boutput.toByteArray();
			} catch (IOException e) {
				// XXX NOT possible
				e.printStackTrace();
				throw new RuntimeException("NOT possible");
			} finally {
				// ByteArrayOutputStream close方法其实是无效的
				if(null != ooutput) {
					try {
						ooutput.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(null != boutput) {
					try {
						boutput.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public Data fromBytes(byte[] bytes) {
			ByteArrayInputStream binput = null;
			ObjectInputStream oinput = null;
			try {
				binput = new ByteArrayInputStream(bytes);
				oinput = new ObjectInputStream(binput);
				return (Data)oinput.readObject();
			} catch (IOException e) {
				// XXX NOT possible
				throw new RuntimeException("NOT possible");
			} catch (ClassNotFoundException e) {
				// XXX NOT possible
				e.printStackTrace();
				throw new RuntimeException("NOT possible");
			} finally {
				// ByteArrayInputStream close方法其实是无效的
				if(null != oinput) {
					try {
						oinput.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(null != binput) {
					try {
						binput.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	};
	
	/**
	 * 
	 */
	protected Cache.Type cacheType = Cache.Type.HYBRID_LRU;
	
	/**
	 * 
	 */
	protected int maxEntries = Cache.DEFAULT_MAX_ENTRIES;
	
	/**
	 * 
	 */
	protected int maxEntriesInMemory = Cache.DEFAULT_MAX_ENTRIES / 2;
	
	/**
	 * 
	 */
	protected String cacheFileName = "/tmp/hybrid.cache";
	
	/**
	 * 
	 * @param cache
	 * @return
	 */
	public AbstractRepositoryClientFactory withCertificate(Certificate certificate) {
		this.certificate = certificate;
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public AbstractRepositoryClientFactory enableCache() {
		this.isEnableCache = true;
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public AbstractRepositoryClientFactory disableCache() {
		this.isEnableCache = false;
		return this;
	}
	
	/**
	 * 
	 * @param maxEntries
	 * @return
	 */
	public AbstractRepositoryClientFactory withCacheType(Cache.Type cacheType) {
		this.cacheType = cacheType;
		return this;
	}
	
	/**
	 * 
	 * @param maxEntries
	 * @return
	 */
	public AbstractRepositoryClientFactory withMaxEntries(int maxEntries) {
		this.maxEntries = maxEntries;
		return this;
	}
	
	/**
	 * 
	 * @param maxEntriesInMemory
	 * @return
	 */
	public AbstractRepositoryClientFactory withMaxEntriesInMemory(int maxEntriesInMemory) {
		this.maxEntriesInMemory = maxEntriesInMemory;
		return this;
	}
	
	/**
	 * 
	 * @param maxEntriesInMemory
	 * @return
	 */
	public AbstractRepositoryClientFactory withCacheFileName(String cacheFileName) {
		this.cacheFileName = cacheFileName;
		return this;
	}
}
