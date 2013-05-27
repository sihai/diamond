/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.common.configuration.Configuration;
import com.galaxy.hsf.common.datastore.DataStore;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.log.Logger;
import com.galaxy.hsf.service.PublishHook;
import com.galaxy.hsf.service.RPCProtocolTemplateHolder;
import com.galaxy.hsf.service.metadata.MetadataService;
import com.galaxy.hsf.service.metadata.Publisher;
import com.galaxy.hsf.service.metadata.ServiceMetadata;

/**
 * 描述：实现服务的发布和调用，控制整个发布流程和生成服务调用代理的流程
 * 
 * @author sihai
 * 
 */
public class PublisherComponent implements Publisher {

	private static Log logger = LogFactory.getLog(Logger.LOGGER_NAME);

	private MetadataService metadataService = null;

	private DataStore dataStore = null;

	private Configuration configuration = null;

	private List<PublishHook> hookList = new ArrayList<PublishHook>();

	public void setDataStore(DataStore dataStore) {
		this.dataStore = dataStore;
	}

	public void unsetDataStore(DataStore dataStore) {
		if (this.dataStore == dataStore) {
			this.dataStore = null;
		}
	}

	public void addHook(PublishHook hook) {
		hookList.add(hook);
	}

	public void unsetHook(PublishHook hook) {
		hookList.remove(hook);
	}

	public void setMetadataService(MetadataService metadataService) {
		this.metadataService = metadataService;
	}

	public void unsetMetadataService(MetadataService metadataService) {
		if (this.metadataService == metadataService) {
			this.metadataService = null;
		}
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void unsetConfiguration(Configuration configuration) {
		if (this.configuration == configuration) {
			this.configuration = null;
		}
	}

	@Override
	public void publish(ServiceMetadata metadata) throws HSFException {
		for (PublishHook hook : hookList) {
			hook.before(metadata);
        }
        if(metadataService == null){
            throw new HSFException("发布HSF服务时必须依赖的Metadataservice不存在！");
        }
        // 交由RPC协议自行完成
        // 根据metadata获取RPC协议，然后调用相应的RPC服务实现来完成服务的发布 
        Map<String, Properties> exporters = metadata.getExporters();
        for (String rpcProtocolType : exporters.keySet()) {
			try {
				RPCProtocolTemplateHolder.get().registerProvider(rpcProtocolType, metadata);
			} catch(HSFException e) {
				logger.fatal("RPC协议：" + rpcProtocolType + "方式发布HSF服务时出现错误，请确认服务：" + metadata.getUniqueName() + "的rpc属性的配置！");
				throw e;
			}
		}

        // 发布服务信息
        metadataService.publish(metadata);
        
        for (PublishHook hook : hookList) {
			hook.after(metadata);
        }
	}

	@Override
	public List<PublishHook> getHooks() {
		return hookList;
	}
}