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
package com.openteach.diamond.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.openteach.diamond.address.AddressingService;
import com.openteach.diamond.address.factory.AddressingServiceFactory;
import com.openteach.diamond.common.resource.ResourceConfig;
import com.openteach.diamond.metadata.MetadataReadService;
import com.openteach.diamond.metadata.MetadataWriteService;
import com.openteach.diamond.metadata.factory.MetadataReadServiceFactory;
import com.openteach.diamond.metadata.factory.MetadataWriteServiceFactory;
import com.openteach.diamond.network.HSFNetworkServer;
import com.openteach.diamond.repository.client.Certificate;
import com.openteach.diamond.repository.client.RepositoryClient;
import com.openteach.diamond.repository.client.cache.Cache;
import com.openteach.diamond.repository.client.factory.AbstractRepositoryClientFactory;
import com.openteach.diamond.repository.client.impl.database.DatabaseCertificate;
import com.openteach.diamond.router.ServiceRouter;
import com.openteach.diamond.router.ServiceRouterFactory;
import com.openteach.diamond.router.plugin.RouterPlugin;
import com.openteach.diamond.router.plugin.random.RandomRouterPlugin;
import com.openteach.diamond.rpc.RPCProtocolProvider;
import com.openteach.diamond.rpc.RPCProtocolProviderFactory;
import com.openteach.diamond.service.impl.DefaultHSFService;
import com.openteach.diamond.service.impl.DefaultServiceInvoker;
import com.openteach.diamond.service.impl.DefaultServiceRegister;
import com.openteach.diamond.service.impl.DefaultServiceSubscriber;
import com.openteach.diamond.service.request.HSFNetworkRequestHandler;
import com.openteach.diamond.service.request.executor.HSFRequestExecutor;
import com.openteach.diamond.service.request.executor.impl.DefaultHSFRequestExecutor;


/**
 * 
 * @author sihai
 *
 */
public class DiamondServiceFactory {

	public static final String HSF_CONFIG_FILE_NAME = "diamond.cnf";
	
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
	
	public static final String REQUEST_HANDLER_RESOURCE_CONFIG_MIN_THREAD_COUNT = "request.handler.resource.config.minThreadCount";
	public static final String REQUEST_HANDLER_RESOURCE_CONFIG_MAX_THREAD_COUNT = "request.handler.resource.config.maxThreadCount";
	public static final String REQUEST_HANDLER_RESOURCE_CONFIG_KEEP_ALIVE_TIME = "request.handler.resource.config.keepAliveTime";
	public static final String REQUEST_HANDLER_RESOURCE_CONFIG_QUEUE_SIZE = "request.handler.resource.config.queue.size";
	
	
	
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
	
	
	private static final DiamondService diamondService;
	
	static {
		diamondService = newDiamondService();
	}
	
	public static DiamondService getDiamondService() {
		return diamondService;
	}
	
	public static void destroy() {
		if(null != diamondService) {
			diamondService.stop();
			diamondService.destroy();
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
		if(null != handler) {
			handler.stop();
			handler.destroy();
		}
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
	private static DiamondService newDiamondService() {
		try {
			// load default first
			Properties properties = new Properties();
			properties.load(DiamondServiceFactory.class.getResourceAsStream(String.format("/cnf/%s", HSF_CONFIG_FILE_NAME)));
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
		String cacheFileName = "/tmp/diamond-repository-local-cache.cache";
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
		ResourceConfig config =  new ResourceConfig();
		String value = properties.getProperty(REQUEST_HANDLER_RESOURCE_CONFIG_MIN_THREAD_COUNT);
		if(StringUtils.isNotBlank(value)) {
			try {
				config.setMinThreadCount(Integer.valueOf(value));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(String.format("Only Positive integer and less then %s allowed for %s", REQUEST_HANDLER_RESOURCE_CONFIG_MIN_THREAD_COUNT, REQUEST_HANDLER_RESOURCE_CONFIG_MAX_THREAD_COUNT));
			}
		}
		value = properties.getProperty(REQUEST_HANDLER_RESOURCE_CONFIG_MAX_THREAD_COUNT);
		if(StringUtils.isNotBlank(value)) {
			try {
				config.setMaxThreadCount(Integer.valueOf(value));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(String.format("Only Positive integer and big then %s allowed for %s", REQUEST_HANDLER_RESOURCE_CONFIG_MAX_THREAD_COUNT, REQUEST_HANDLER_RESOURCE_CONFIG_MIN_THREAD_COUNT));
			}
		}
		if(config.getMaxThreadCount() < config.getMinThreadCount()) {
			throw new IllegalArgumentException(String.format("Only Positive integer and big then %s allowed for %s", REQUEST_HANDLER_RESOURCE_CONFIG_MAX_THREAD_COUNT, REQUEST_HANDLER_RESOURCE_CONFIG_MIN_THREAD_COUNT));
		}
		value = properties.getProperty(REQUEST_HANDLER_RESOURCE_CONFIG_KEEP_ALIVE_TIME);
		if(StringUtils.isNotBlank(value)) {
			try {
				config.setMaxThreadCount(Integer.valueOf(value));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(String.format("Only Positive integer allowed for %s", REQUEST_HANDLER_RESOURCE_CONFIG_KEEP_ALIVE_TIME));
			}
		}
		value = properties.getProperty(REQUEST_HANDLER_RESOURCE_CONFIG_QUEUE_SIZE);
		if(StringUtils.isNotBlank(value)) {
			try {
				config.setQueueSize(Integer.valueOf(value));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(String.format("Only Positive integer allowed for %s", REQUEST_HANDLER_RESOURCE_CONFIG_QUEUE_SIZE));
			}
		}
		HSFNetworkRequestHandler handler = new HSFNetworkRequestHandler(executor, config);
		handler.initialize();
		handler.start();
		return handler;
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
		DiamondService diamondService = DiamondServiceFactory.getDiamondService();
	}
}
