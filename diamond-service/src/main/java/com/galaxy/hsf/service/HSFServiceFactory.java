/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.galaxy.hsf.repository.client.Certificate;
import com.galaxy.hsf.repository.client.RepositoryClient;
import com.galaxy.hsf.repository.client.cache.Cache;
import com.galaxy.hsf.repository.client.factory.AbstractRepositoryClientFactory;
import com.galaxy.hsf.repository.client.impl.database.DatabaseCertificate;


/**
 * 
 * @author sihai
 *
 */
public class HSFServiceFactory {

	public static final String HSF_CONFIG_FILE_NAME = "hsf.cnf";
	
	public static final String REPOSITORY_CLIENT_FACTORY = "repository.client.factory";
	
	public static final String REPOSITORY_CLIENT_LOCAL_CACHE_ENABLE = "repository.client.local.cache.enable";
	
	public static final String REPOSITORY_CLIENT_LOCAL_CACHE_TYPE = "repository.client.local.cache.type";
	
	public static final String REPOSITORY_CLIENT_LOCAL_CACHE_MAX_ENTRIES = "repository.client.local.cache.maxEntries";
	
	public static final String REPOSITORY_CLIENT_LOCAL_CACHE_MAX_ENTRIES_IN_MEMORY = "repository.client.local.cache.maxEntriesInMemory";
	
	public static final String REPOSITORY_CLIENT_LOCAL_CACHE_FILE_NAME = "repository.client.local.cache.fileName";
	
	public static final String REPOSITORY_CLIENT_CERTIFICATE_TYPE = "repository.client.certificate.type";
	
	public static final String REPOSITORY_CLIENT_CERTIFICATE_TYPE_DATABASE = "database";
	
	public static final String REPOSITORY_CLIENT_CERTIFICATE_TYPE_ZOOKEEPER = "zookeeper";
	
	public static final String REPOSITORY_CLIENT_CERTIFICATE_APP_NAME = "repository.client.certificate.appName";
	
	public static final String REPOSITORY_CLIENT_CERTIFICATE_SECRET = "repository.client.certificate.secret";
	
	public static final String REPOSITORY_CLIENT_CERTIFICATE_NAMESPACE = "repository.client.certificate.namespace";
	
	public static final String REPOSITORY_CLIENT_CERTIFICATE_USER_NAME = "repository.client.certificate.username";
	
	public static final String REPOSITORY_CLIENT_CERTIFICATE_PASSWORD = "repository.client.certificate.password";
	
	public static final String REPOSITORY_CLIENT_CERTIFICATE_URL = "repository.client.certificate.url";
	
	public static final String REPOSITORY_CLIENT_CERTIFICATE_DRIVER_CLASS_NAME = "repository.client.certificate.driverClassName";
	
	
	private static final HSFService hsfService;
	
	static {
		hsfService = newHSFService();
	}
	
	public static HSFService getHSFService() {
		return hsfService;
	}
	
	/**
	 * 
	 * @return
	 */
	private static HSFService newHSFService() {
		try {
			// load default first
			Properties properties = new Properties();
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(String.format("/cnf/%s", HSF_CONFIG_FILE_NAME)));
			// then try to load custom to override
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(HSF_CONFIG_FILE_NAME);
			if(null != in) {
				properties.load(in);
			}
			
			// Repository
			RepositoryClient repositoryClient = newRepositoryClient(properties);
			
			// 
		} catch (IOException e) {
			
		}
	}
	
	/**
	 * 
	 * @param properties
	 * @return
	 */
	private static RepositoryClient newRepositoryClient(Properties properties) {
		String factoryClassName = StringUtils.trim(properties.getProperty(REPOSITORY_CLIENT_FACTORY));
		if(StringUtils.isBlank(factoryClassName)) {
			throw new IllegalArgumentException(String.format("Please set %s", REPOSITORY_CLIENT_FACTORY));
		}
		// cache
		boolean eableLocalCache = false;
		String value = StringUtils.trim(properties.getProperty(REPOSITORY_CLIENT_LOCAL_CACHE_ENABLE));
		if(StringUtils.isNotBlank(value)) {
			if(StringUtils.equals(Boolean.TRUE.toString(), value)) {
				eableLocalCache = true;
			} else if(StringUtils.equals(Boolean.FALSE.toString(), value)) {
				eableLocalCache = false;
			} else {
				throw new IllegalArgumentException(String.format("Only %s or %s allowed for %s", Boolean.TRUE.toString(), Boolean.FALSE.toString(), REPOSITORY_CLIENT_LOCAL_CACHE_ENABLE));
			}
		}
		Cache.Type type = Cache.Type.MEMORY_LRU;
		int maxEntries = Cache.DEFAULT_MAX_ENTRIES;
		int maxEntriesInMemory = maxEntries / 5;
		String cacheFileName = "/tmp/hsf-repository-local-cache.cache";
		if(eableLocalCache) {
			value = StringUtils.trim(properties.getProperty(REPOSITORY_CLIENT_LOCAL_CACHE_TYPE));
			if(StringUtils.isNotBlank(value)) {
				type = Cache.Type.valueOf(value);
				if(null == type) {
					throw new IllegalArgumentException(String.format("Only %s allowed for %s", StringUtils.join(Cache.Type.values(), ","), REPOSITORY_CLIENT_LOCAL_CACHE_TYPE));
				}
			}
			value = StringUtils.trim(properties.getProperty(REPOSITORY_CLIENT_LOCAL_CACHE_MAX_ENTRIES));
			if(StringUtils.isNotBlank(value)) {
				try {
					maxEntries = Integer.valueOf(value);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(String.format("Only Positive integer allowed for %s", REPOSITORY_CLIENT_LOCAL_CACHE_MAX_ENTRIES));
				}
				if(maxEntries <= 0) {
					throw new IllegalArgumentException(String.format("Only Positive integer allowed for %s", REPOSITORY_CLIENT_LOCAL_CACHE_MAX_ENTRIES));
				}
			}
			if(Cache.Type.HYBRID_LRU == type) {
				value = StringUtils.trim(properties.getProperty(REPOSITORY_CLIENT_LOCAL_CACHE_MAX_ENTRIES_IN_MEMORY));
				if(StringUtils.isNotBlank(value)) {
					try {
						maxEntriesInMemory = Integer.valueOf(value);
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException(String.format("Only Positive integer and less then %s allowed for %s", REPOSITORY_CLIENT_LOCAL_CACHE_MAX_ENTRIES_IN_MEMORY, REPOSITORY_CLIENT_LOCAL_CACHE_MAX_ENTRIES));
					}
					if(maxEntriesInMemory <= 0 || maxEntriesInMemory >= maxEntries) {
						throw new IllegalArgumentException(String.format("Only Positive integer and less then %s allowed for %s", REPOSITORY_CLIENT_LOCAL_CACHE_MAX_ENTRIES_IN_MEMORY, REPOSITORY_CLIENT_LOCAL_CACHE_MAX_ENTRIES));
					}
				}
			}
			
			if(Cache.Type.FILE_LRU == type || Cache.Type.HYBRID_LRU == type) {
				value = StringUtils.trim(properties.getProperty(REPOSITORY_CLIENT_LOCAL_CACHE_FILE_NAME));
				if(StringUtils.isBlank(value)) {
					cacheFileName = value;
				}
			}
		}
		
		// certificate
		Certificate certificate = null;
		value = StringUtils.trim(properties.getProperty(REPOSITORY_CLIENT_CERTIFICATE_TYPE));
		if(StringUtils.isBlank(value)) {
			throw new IllegalArgumentException(String.format("Please set %s, value: %s or %s", REPOSITORY_CLIENT_CERTIFICATE_TYPE, REPOSITORY_CLIENT_CERTIFICATE_TYPE_DATABASE, REPOSITORY_CLIENT_CERTIFICATE_TYPE_ZOOKEEPER));
		}
		if(StringUtils.equals(REPOSITORY_CLIENT_CERTIFICATE_TYPE_DATABASE, value)) {
			certificate = new DatabaseCertificate();
			DatabaseCertificate dc = (DatabaseCertificate)certificate;
			value = StringUtils.trim(properties.getProperty(REPOSITORY_CLIENT_CERTIFICATE_USER_NAME));
			if(StringUtils.isBlank(value)) {
				throw new IllegalArgumentException(String.format("Please set %s, value type: string", REPOSITORY_CLIENT_CERTIFICATE_USER_NAME));
			}
			dc.setUsername(value);
			
			value = StringUtils.trim(properties.getProperty(REPOSITORY_CLIENT_CERTIFICATE_PASSWORD));
			if(StringUtils.isBlank(value)) {
				throw new IllegalArgumentException(String.format("Please set %s, value type: string", REPOSITORY_CLIENT_CERTIFICATE_PASSWORD));
			}
			dc.setPassword(value);
			
			value = StringUtils.trim(properties.getProperty(REPOSITORY_CLIENT_CERTIFICATE_URL));
			if(StringUtils.isBlank(value)) {
				throw new IllegalArgumentException(String.format("Please set %s, value type: string", REPOSITORY_CLIENT_CERTIFICATE_URL));
			}
			dc.setUrl(value);
			
			value = StringUtils.trim(properties.getProperty(REPOSITORY_CLIENT_CERTIFICATE_DRIVER_CLASS_NAME));
			if(StringUtils.isBlank(value)) {
				throw new IllegalArgumentException(String.format("Please set %s, value type: string", REPOSITORY_CLIENT_CERTIFICATE_DRIVER_CLASS_NAME));
			}
			dc.setDriverClassName(value);
			
			// TODO pool setting
		} else if(StringUtils.equals(REPOSITORY_CLIENT_CERTIFICATE_TYPE_ZOOKEEPER, value)) {
			throw new IllegalArgumentException(String.format("At now we not supported %s", REPOSITORY_CLIENT_CERTIFICATE_TYPE_ZOOKEEPER));
		} else {
			throw new IllegalArgumentException(String.format("Only %s or %s allowed for %s", REPOSITORY_CLIENT_CERTIFICATE_TYPE_DATABASE, REPOSITORY_CLIENT_CERTIFICATE_TYPE_ZOOKEEPER, REPOSITORY_CLIENT_CERTIFICATE_TYPE));
		}
		
		value = StringUtils.trim(properties.getProperty(REPOSITORY_CLIENT_CERTIFICATE_APP_NAME));
		if(StringUtils.isBlank(value)) {
			throw new IllegalArgumentException(String.format("Please set %s, value type: string", REPOSITORY_CLIENT_CERTIFICATE_APP_NAME));
		}
		certificate.setAppName(value);
		
		value = StringUtils.trim(properties.getProperty(REPOSITORY_CLIENT_CERTIFICATE_SECRET));
		if(StringUtils.isBlank(value)) {
			throw new IllegalArgumentException(String.format("Please set %s, value type: string", REPOSITORY_CLIENT_CERTIFICATE_SECRET));
		}
		certificate.setSecret(value);
		
		value = StringUtils.trim(properties.getProperty(REPOSITORY_CLIENT_CERTIFICATE_NAMESPACE));
		if(StringUtils.isBlank(value)) {
			throw new IllegalArgumentException(String.format("Please set %s, value type: string", REPOSITORY_CLIENT_CERTIFICATE_NAMESPACE));
		}
		certificate.setNamespace(value);
		
		Class clazz = Class.forName(factoryClassName);
		AbstractRepositoryClientFactory factory = (AbstractRepositoryClientFactory)clazz.newInstance();
		if(eableLocalCache) {
			factory.enableCache().withCacheType(type);
			factory.withMaxEntries(maxEntries);
			if(Cache.Type.FILE_LRU == type) {
				factory.withCacheFileName(cacheFileName);
			} else if(Cache.Type.HYBRID_LRU == type) {
				factory.withMaxEntriesInMemory(maxEntriesInMemory).withCacheFileName(cacheFileName);
			}
		} else {
			factory.disableCache();
		}
		return factory.withCertificate(certificate).newInstace();
	}
}
