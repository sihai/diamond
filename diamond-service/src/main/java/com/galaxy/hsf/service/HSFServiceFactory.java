/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.galaxy.diamond.metadata.MetadataReadService;
import com.galaxy.diamond.metadata.MetadataWriteService;
import com.galaxy.diamond.metadata.factory.MetadataReadServiceFactory;
import com.galaxy.diamond.metadata.factory.MetadataWriteServiceFactory;
import com.galaxy.hsf.address.AddressingService;
import com.galaxy.hsf.address.factory.AddressingServiceFactory;
import com.galaxy.hsf.network.HSFNetworkServer;
import com.galaxy.hsf.repository.client.Certificate;
import com.galaxy.hsf.repository.client.RepositoryClient;
import com.galaxy.hsf.repository.client.cache.Cache;
import com.galaxy.hsf.repository.client.factory.AbstractRepositoryClientFactory;
import com.galaxy.hsf.repository.client.impl.database.DatabaseCertificate;
import com.galaxy.hsf.router.ServiceRouter;
import com.galaxy.hsf.router.ServiceRouterFactory;
import com.galaxy.hsf.router.plugin.RouterPlugin;
import com.galaxy.hsf.router.plugin.random.RandomRouterPlugin;
import com.galaxy.hsf.rpc.RPCProtocolProvider;
import com.galaxy.hsf.rpc.RPCProtocolProviderFactory;
import com.galaxy.hsf.service.impl.DefaultHSFService;
import com.galaxy.hsf.service.impl.DefaultServiceInvoker;
import com.galaxy.hsf.service.impl.DefaultServiceRegister;
import com.galaxy.hsf.service.impl.DefaultServiceSubscriber;
import com.galaxy.hsf.service.request.HSFNetworkRequestHandler;
import com.galaxy.hsf.service.request.executor.HSFRequestExecutor;
import com.galaxy.hsf.service.request.executor.impl.DefaultHSFRequestExecutor;


/**
 * 
 * @author sihai
 *
 */
public class HSFServiceFactory {

	public static final String HSF_CONFIG_FILE_NAME = "hsf.cnf";
	
	// 
	
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
	
	
	//
	public static final String METADATA_READ_FACTORY = "metadata.read.factory";
	
	public static final String METADATA_WRITE_FACTORY = "metadata.write.factory";
	
	public static final String ADDRESSING_FACTORY = "addressing.factory";
	
	public static final String RPC_PROVIDER_FACTORY = "rpc.provider.factory";
	
	public static final String ROUTER_PLUGIN = "router.plugin";
	
	public static final String ROUTER_FACTORY = "router.factory";
	
	// 
	private static RepositoryClient repositoryClient;
	
	// 
	private static MetadataReadService metadataReadService;
	private static MetadataWriteService metadataWriteService;
	
	// 
	private static AddressingService addressingService;
	
	//
	private static HSFRequestExecutor executor;
	
	private static HSFNetworkServer.NetworkRequestHandler handler;
	
	//
	private static RPCProtocolProvider rpcProtocolProvider;
	
	//
	private static RouterPlugin plugin;
	
	private static ServiceRouter router;
	
	// 
	private static DefaultServiceRegister register;
	private static DefaultServiceSubscriber subscriber;
	private static DefaultServiceInvoker invoker;
	
	
	private static final HSFService hsfService;
	
	static {
		hsfService = newHSFService();
	}
	
	public static HSFService getHSFService() {
		return hsfService;
	}
	
	public static void destroy() {
		if(null != hsfService) {
			hsfService.stop();
			hsfService.destroy();
		}
		// 
		if(null != invoker) {
			invoker.stop();
			invoker.destroy();
		}
		if(null != subscriber) {
			subscriber.stop();
			subscriber.destroy();
		}
		if(null != register) {
			register.stop();
			register.destroy();
		}
		if(null != router) {
			router.stop();
			router.destroy();
		}
		if(null != plugin) {
			plugin.stop();
			plugin.destroy();
		}
		if(null != rpcProtocolProvider) {
			rpcProtocolProvider.stop();
			rpcProtocolProvider.destroy();
		}
		/*if(null != handler) {
			handler.stop();
			handler.destroy();
		}*/
		if(null != executor) {
			executor.stop();
			executor.destroy();
		}
		if(null != addressingService) {
			addressingService.stop();
			addressingService.destroy();
		}
		if(null != metadataWriteService) {
			metadataWriteService.stop();
			metadataWriteService.destroy();
		}
		if(null != metadataReadService) {
			metadataReadService.stop();
			metadataReadService.destroy();
		}
		if(null != repositoryClient) {
			repositoryClient.stop();
			repositoryClient.destroy();
		}
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
			repositoryClient = newRepositoryClient(properties);
			
			// Metadata
			metadataReadService = newMetadataReadService(properties, repositoryClient);
			metadataWriteService = newMetadataWriteService(properties, repositoryClient);
			
			// Addressing
			addressingService = newAddressingService(properties, metadataReadService); 
			
			// Network
			// RPC will initialize network if need
			executor = newHSFRequestExecutor(properties);
			handler = newNetworkRequestHandler(properties, executor);
			
			// RPC
			rpcProtocolProvider = newRPCProtocolProvider(properties, handler);
			
			// Router
			// Plugin
			plugin = newRouterPlugin(properties, repositoryClient);
			router = newServiceRouter(properties, addressingService, plugin);
			
			// 
			register = new DefaultServiceRegister(rpcProtocolProvider, metadataWriteService);
			register.initialize();
			register.start();
			
			// 
			subscriber = new DefaultServiceSubscriber(rpcProtocolProvider, addressingService);
			subscriber.initialize();
			subscriber.start();
						
			// 
			invoker = new DefaultServiceInvoker(router, rpcProtocolProvider);
			invoker.initialize();
			invoker.start();
			
			return new DefaultHSFService(register, subscriber, invoker);
			
		} catch (IOException e) {
			throw new RuntimeException("Create HSF Service failed", e);
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
		
		try {
			Class<?> clazz = Class.forName(factoryClassName);
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
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("OOPS, new repository client failed", e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("OOPS, new repository client failed", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("OOPS, new repository client failed", e);
		}
	}
	
	/**
	 * 
	 * @param properties
	 * @param repositoryClient
	 * @return
	 */
	private static MetadataReadService newMetadataReadService(Properties properties, RepositoryClient repositoryClient) {
		String factoryClassName = StringUtils.trim(properties.getProperty(METADATA_READ_FACTORY));
		if(StringUtils.isBlank(factoryClassName)) {
			throw new IllegalArgumentException(String.format("Please set %s", METADATA_READ_FACTORY));
		}
		try {
			Class<?> clazz = Class.forName(factoryClassName);
			MetadataReadServiceFactory factory = (MetadataReadServiceFactory)clazz.newInstance();
			return factory.newMetadataReadService(repositoryClient);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("OOPS, new metadata read service failed", e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("OOPS, new metadata read service failed", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("OOPS, new metadata read service failed", e);
		}
	}
	
	/**
	 * 
	 * @param properties
	 * @param repositoryClient
	 * @return
	 */
	private static MetadataWriteService newMetadataWriteService(Properties properties, RepositoryClient repositoryClient) {
		String factoryClassName = StringUtils.trim(properties.getProperty(METADATA_WRITE_FACTORY));
		if(StringUtils.isBlank(factoryClassName)) {
			throw new IllegalArgumentException(String.format("Please set %s", METADATA_WRITE_FACTORY));
		}
		try {
			Class<?> clazz = Class.forName(factoryClassName);
			MetadataWriteServiceFactory factory = (MetadataWriteServiceFactory)clazz.newInstance();
			return factory.newMetadataWriteService(repositoryClient);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("OOPS, new metadata write service failed", e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("OOPS, new metadata write service failed", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("OOPS, new metadata write service failed", e);
		}
	}
	
	/**
	 * 
	 * @param properties
	 * @param metadataReadService
	 * @return
	 */
	private static AddressingService newAddressingService(Properties properties, MetadataReadService metadataReadService) {
		String factoryClassName = StringUtils.trim(properties.getProperty(ADDRESSING_FACTORY));
		if(StringUtils.isBlank(factoryClassName)) {
			throw new IllegalArgumentException(String.format("Please set %s", ADDRESSING_FACTORY));
		}
		try {
			Class<?> clazz = Class.forName(factoryClassName);
			AddressingServiceFactory factory = (AddressingServiceFactory)clazz.newInstance();
			return factory.newAddressingService(metadataReadService);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("OOPS, new addressing service failed", e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("OOPS, new addressing service failed", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("OOPS, new addressing service failed", e);
		}
	}
	
	/**
	 * 
	 * @param properties
	 * @return
	 */
	private static HSFRequestExecutor newHSFRequestExecutor(Properties properties) {
		// FIXME
		return new DefaultHSFRequestExecutor();
	}
	
	/**
	 * 
	 * @param properties
	 * @param factory
	 * @param executor
	 * @return
	 */
	private static HSFNetworkServer.NetworkRequestHandler newNetworkRequestHandler(Properties properties, HSFRequestExecutor executor) {
		// FIXME
		return new HSFNetworkRequestHandler(executor);
	}
	/**
	 * 
	 * @param properties
	 * @param handler
	 * @return
	 */
	private static RPCProtocolProvider newRPCProtocolProvider(Properties properties, HSFNetworkServer.NetworkRequestHandler handler) {
		String factoryClassName = StringUtils.trim(properties.getProperty(RPC_PROVIDER_FACTORY));
		if(StringUtils.isBlank(factoryClassName)) {
			throw new IllegalArgumentException(String.format("Please set %s", RPC_PROVIDER_FACTORY));
		}
		try {
			Class<?> clazz = Class.forName(factoryClassName);
			RPCProtocolProviderFactory factory = (RPCProtocolProviderFactory)clazz.newInstance();
			return factory.newRPCProtocolProvider(handler);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("OOPS, new addressing service failed", e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("OOPS, new addressing service failed", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("OOPS, new addressing service failed", e);
		}
	}
	
	/**
	 * 
	 * @param properties
	 * @param repositoryClient
	 */
	private static RouterPlugin newRouterPlugin(Properties properties, RepositoryClient repositoryClient) {
		// FIXME
		RandomRouterPlugin p = new RandomRouterPlugin();
		p.initialize();
		p.start();
		return p;
	}
	
	/**
	 * 
	 * @param properties
	 * @param repositoryClient
	 * @return
	 */
	private static ServiceRouter newServiceRouter(Properties properties, AddressingService addressingService, RouterPlugin plugin) {
		String factoryClassName = StringUtils.trim(properties.getProperty(ROUTER_FACTORY));
		if(StringUtils.isBlank(factoryClassName)) {
			throw new IllegalArgumentException(String.format("Please set %s", ROUTER_FACTORY));
		}
		try {
			Class<?> clazz = Class.forName(factoryClassName);
			ServiceRouterFactory factory = (ServiceRouterFactory)clazz.newInstance();
			return factory.newServiceRouter(addressingService, plugin);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("OOPS, new router failed", e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("OOPS, new router failed", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("OOPS, new router failed", e);
		}
	}
	
	
	public static void main(String[] args) {
		HSFService hsfService = HSFServiceFactory.getHSFService();
	}
}
