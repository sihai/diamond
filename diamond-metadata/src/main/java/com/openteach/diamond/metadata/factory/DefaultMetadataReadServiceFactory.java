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
package com.openteach.diamond.metadata.factory;

import com.openteach.diamond.metadata.MetadataReadService;
import com.openteach.diamond.metadata.impl.DefaultMetadataReadService;
import com.openteach.diamond.repository.client.RepositoryClient;

/**
 * 
 * @author sihai
 *
 */
public class DefaultMetadataReadServiceFactory implements MetadataReadServiceFactory {
	
	@Override
	public MetadataReadService newMetadataReadService(RepositoryClient repositoryClient) {
		DefaultMetadataReadService instance = new DefaultMetadataReadService(repositoryClient);
		instance.initialize();
		instance.start();
		return instance;
	}

}
