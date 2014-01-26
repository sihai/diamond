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
 */
package com.openteach.diamond.metadata.impl;

import java.net.MalformedURLException;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.common.exception.DiamondException;
import com.openteach.diamond.metadata.Listener;
import com.openteach.diamond.metadata.MetadataReadService;
import com.openteach.diamond.metadata.ServiceMetadata;
import com.openteach.diamond.metadata.ServiceURL;
import com.openteach.diamond.repository.client.Data;
import com.openteach.diamond.repository.client.DataEvent;
import com.openteach.diamond.repository.client.Key;
import com.openteach.diamond.repository.client.RepositoryClient;

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
		List<Data> dList = repositoryClient.mget(key, new com.openteach.diamond.repository.client.listener.AbstractListener(key) {

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
		
		if(null == dList || dList.isEmpty()) {
			throw new DiamondException(String.format("Can not found service:%s", serviceName));
		}
		
		ServiceURL url = null;
		ServiceMetadata sm = new ServiceMetadata();
		for(Data d : dList) {
			try {
				url = new ServiceURL((String)d.getValue());
				sm.addAddress(url.getProtocol(), url);
			} catch (MalformedURLException e) {
				logger.error(String.format("Received one wrong service url:%s", d.getValue()));
			}
		}
		return sm;
	}

	@Override
	public void register(Listener listener) {
		this.listener = listener;
	}
}
