/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.metadata.impl;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.diamond.metadata.Listener;
import com.galaxy.diamond.metadata.MetadataReadService;
import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.diamond.repository.client.Data;
import com.galaxy.diamond.repository.client.DataEvent;
import com.galaxy.diamond.repository.client.Key;
import com.galaxy.diamond.repository.client.RepositoryClient;
import com.galaxy.hsf.common.exception.HSFException;

/**
 * 
 * @author sihai
 *
 */
public class DefaultMetadataReadService extends AbstractMetadataService implements MetadataReadService {

	private Log logger = LogFactory.getLog(DefaultMetadataReadService.class);
	
	/**
	 * 
	 */
	protected volatile Listener listener;
	
	/**
	 * 
	 * @param repositoryClient
	 */
	public DefaultMetadataReadService(RepositoryClient repositoryClient) {
		super(repositoryClient);
	}

	@Override
	public ServiceMetadata subscribe(String serviceName) throws HSFException {
		Key key = repositoryClient.newKey(serviceName);
		Data data = repositoryClient.get(key, new com.galaxy.diamond.repository.client.listener.AbstractListener(key) {

			@Override
			public void changed(DataEvent event) {
				String value = (String)event.getNewOne().getValue();
				ServiceMetadata metadata = (ServiceMetadata)JSONObject.toBean(JSONObject.fromObject(value), ServiceMetadata.class);
				Listener l = listener;
				if(null != l) {
					l.changed(metadata);
				}
			}
			
		});
		
		if(null == data) {
			throw new HSFException(String.format("Can not found service:%s", serviceName));
		}
		
		return (ServiceMetadata)JSONObject.toBean(JSONObject.fromObject((String)data.getValue()), ServiceMetadata.class);
	}

	@Override
	public void register(Listener listener) {
		this.listener = listener;
	}
}
