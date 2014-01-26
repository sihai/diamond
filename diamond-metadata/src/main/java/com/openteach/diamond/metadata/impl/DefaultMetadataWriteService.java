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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.metadata.MetadataWriteService;
import com.openteach.diamond.metadata.PublishHook;
import com.openteach.diamond.metadata.ServiceMetadata;
import com.openteach.diamond.metadata.ServiceURL;
import com.openteach.diamond.repository.client.Data;
import com.openteach.diamond.repository.client.Key;
import com.openteach.diamond.repository.client.RepositoryClient;
import com.openteach.diamond.repository.client.exception.SequenceNotMatchException;

/**
 * 
 * @author sihai
 *
 */
public class DefaultMetadataWriteService extends AbstractMetadataService implements MetadataWriteService {

	private Log logger = LogFactory.getLog(DefaultMetadataWriteService.class);
	
	/**
	 * 
	 * @param repositoryClient
	 */
	public DefaultMetadataWriteService(RepositoryClient repositoryClient) {
		super(repositoryClient);
	}

	@Override
	public void register(ServiceMetadata metadata, List<ServiceURL> exportedProtocols) {
		for(int i = 0;; i++) {
			try {
				_put_(metadata, exportedProtocols);
				break;
			} catch (SequenceNotMatchException e) {
				logger.warn(String.format("Put metadata failed, sequence not match, so try do it again, tryed %d times", i), e);
			}
		}
	}

	@Override
	public void unregister(ServiceMetadata metadata) {
		for(int i = 0;; i++) {
			try {
				_delete_(metadata);
				break;
			} catch (SequenceNotMatchException e) {
				logger.warn(String.format("Put metadata failed, sequence not match, so try do it again, tryed %d times", i), e);
			}
		}
	}

	@Override
	public void addPublishHook(PublishHook hook) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param metadata
	 * @param exportedProtocols
	 * @throws SequenceNotMatchException
	 */
	private void _put_(ServiceMetadata metadata, List<ServiceURL> exportedProtocols) throws SequenceNotMatchException {
		String name = metadata.getUniqueName();
		for(ServiceURL url : exportedProtocols) {
			Key key = repositoryClient.newKey(name, url.getProtocol());
			Data data = repositoryClient.newData(key, url.toString());
			repositoryClient.put(data);
		}
	}
	
	/**
	 * 
	 * @param metadata
	 * @throws SequenceNotMatchException 
	 */
	private void _delete_(ServiceMetadata metadata) throws SequenceNotMatchException {
		String name = metadata.getUniqueName();
		for(String protocol : metadata.getExportProtocols().keySet()) {
			repositoryClient.delete(repositoryClient.newKey(name, protocol));
		}
	}
}
