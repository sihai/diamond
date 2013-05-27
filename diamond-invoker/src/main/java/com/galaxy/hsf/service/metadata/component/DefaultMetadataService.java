/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.metadata.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.common.configuration.Configuration;
import com.galaxy.hsf.common.log.Logger;
import com.galaxy.hsf.event.HSFEvent;
import com.galaxy.hsf.event.HSFEventListener;
import com.galaxy.hsf.repository.client.Data;
import com.galaxy.hsf.repository.client.DataEvent;
import com.galaxy.hsf.repository.client.Key;
import com.galaxy.hsf.repository.client.RepositoryClient;
import com.galaxy.hsf.repository.client.listener.AbstractListener;
import com.galaxy.hsf.rpc.RPCProtocol;
import com.galaxy.hsf.service.RPCProtocolTemplateHolder;
import com.galaxy.hsf.service.address.AddressService;
import com.galaxy.hsf.service.metadata.MetadataService;
import com.galaxy.hsf.service.metadata.MethodSpecial;
import com.galaxy.hsf.service.metadata.ServiceMetadata;
import com.galaxy.hsf.service.util.HSFServiceTargetUtil;

/**
 * 
 * @author sihai
 *
 */
public class DefaultMetadataService implements MetadataService {

	static private Log logger = LogFactory.getLog(Logger.LOGGER_NAME);
	
	static private final String PUBLISHER_PREFIX = "HSFProvider-";
	static private final String SUBSCRIBER_PREFIX = "HSFSubscriber-";
	
    private AddressService addressService; 
    
    private Configuration configuration;
    
    private RepositoryClient repositoryClient;
    
	private List<HSFEventListener> eventListeners = new ArrayList<HSFEventListener>();
    
    public void register(HSFEventListener hsfEventListener ){
    	eventListeners.add(hsfEventListener);
    }
    
    public void unregister(HSFEventListener hsfEventListener ){
    	eventListeners.remove(hsfEventListener);
    }

    public void setAddressService(AddressService addressService){
    	this.addressService=addressService;
    }
    
    public void setConfiguration(Configuration configuration){
    	this.configuration = configuration;
    }
    
    public void setRepositoryClient(RepositoryClient repositoryClient) {
		this.repositoryClient = repositoryClient;
	}
    
	@Override
	public void publish(ServiceMetadata metadata) {
		for(Map.Entry<String, Properties> entry : metadata.getExporters().entrySet()) {
			// RPC协议
            String protocol = entry.getKey();
            // RPC特有属性
            Properties rpcProperties = entry.getValue();
            // 发布端口
            RPCProtocol rpcProtocol = RPCProtocolTemplateHolder.get().getRPCProtocol(protocol);
            int port = 0;
            if(rpcProtocol != null) {
                port = rpcProtocol.getPublishPort();
            } else {
            	throw new IllegalArgumentException(String.format("Unsupported RPC protocol:%s", protocol));
            }
            // XXX
            if (port <= 255)
                continue;
            // 公共属性
            Properties commonProperties = metadata.getServiceProperties();
            // methodSpecial
            MethodSpecial[] methodSpecials = metadata.getMethodSpecials();
            // 取得的发布端口是否有效

            // 在服务的唯一标识上连接协议头
            String serviceName = metadata.getUniqueNameWithProtocol(protocol);
            // 构造统一格式的HSF调用地址
            String serviceAddress = HSFServiceTargetUtil.getTarget(port, commonProperties, rpcProperties, methodSpecials);
            // 向配置中心发布
            repositoryClient.put(repositoryClient.newData(repositoryClient.newKey(String.format("%s%s", PUBLISHER_PREFIX, serviceName)), serviceAddress));
		}
	}

	@Override
	public void subscriber(ServiceMetadata metadata) {
		
        String protocol = metadata.selectImporter();
        if(null == protocol){
        	throw new IllegalArgumentException("Protocol must not be null");
        }
        
        final String serviceName = metadata.getUniqueNameWithProtocol(protocol);
        
        // 订阅服务地址信息
        Key key = repositoryClient.newKey(String.format("%s%s.address", SUBSCRIBER_PREFIX, serviceName));
        Data data = repositoryClient.get(key, new AbstractListener(key) {

			@Override
			public void changed(DataEvent event) {
				addressService.setServiceAddresses(serviceName, asList((String)event.getNewOne().getValue()));
				fireHSFEvent(HSFEvent.EventType.SERVICE_ADDRESS_CHANGED, event.getNewOne().getValue());
			}
        	
        });
        
        // 订阅服务路由信息
        key = repositoryClient.newKey(String.format("%s%s.route", SUBSCRIBER_PREFIX, serviceName));
        data = repositoryClient.get(key, new AbstractListener(key) {

			@Override
			public void changed(DataEvent event) {
				addressService.setServiceRouteRule(serviceName, asList((String)event.getNewOne().getValue()));
				fireHSFEvent(HSFEvent.EventType.SERVICE_ROUTE_CHANGED, event.getNewOne().getValue());
			}
        	
        });
        
        // 权重
        key = repositoryClient.newKey(String.format("%s%s.weight", SUBSCRIBER_PREFIX, serviceName));
        data = repositoryClient.get(key, new AbstractListener(key) {

			@Override
			public void changed(DataEvent event) {
				addressService.setServiceWeightRule(serviceName, (String)event.getNewOne().getValue());
				fireHSFEvent(HSFEvent.EventType.SERVICE_WEIGHT_RULE_CHANGED, event.getNewOne().getValue());
			}
        	
        });
	}

	@Override
	public void unregister(ServiceMetadata metadata) {
		repositoryClient.delete(repositoryClient.newKey(metadata.getUniqueName()));
	}

	@Override
	public void register(ServiceMetadata metadata) {
		repositoryClient.put(repositoryClient.newData(metadata.getUniqueName(), HSFServiceTargetUtil.getTarget(configuration.getHSFServerPort(), metadata.getServiceProperties(),metadata.getExporters().get("DEFAULT"),metadata.getMethodSpecials())));
	}
	
	/**
	 * 
	 * @param type
	 * @param data
	 */
	private void fireHSFEvent(HSFEvent.EventType type, Object data){
		HSFEvent event = new HSFEvent(type, data);
		for(HSFEventListener listener :eventListeners){
			try {
				listener.onEvent(event);
			} catch (Throwable t) {
				t.printStackTrace();
				logger.error("Fire hsf event failed", t);
			}
		}
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	private List<String> asList(String value) {
		return Arrays.asList(StringUtils.strip(value));
	}
}
