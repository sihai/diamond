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
 */
package com.galaxy.diamond.metadata.impl;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.diamond.common.exception.DiamondException;
import com.galaxy.diamond.metadata.Listener;
import com.galaxy.diamond.metadata.MetadataReadService;
import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.diamond.repository.client.Data;
import com.galaxy.diamond.repository.client.DataEvent;
import com.galaxy.diamond.repository.client.Key;
import com.galaxy.diamond.repository.client.RepositoryClient;

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
	public ServiceMetadata subscribe(String serviceName) throws DiamondException {
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
			throw new DiamondException(String.format("Can not found service:%s", serviceName));
		}
		
		return (ServiceMetadata)JSONObject.toBean(JSONObject.fromObject((String)data.getValue()), ServiceMetadata.class);
	}

	@Override
	public void register(Listener listener) {
		this.listener = listener;
	}
}
