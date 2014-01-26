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
package com.openteach.diamond.metadata.impl;

import com.openteach.diamond.common.lifecycle.AbstractLifeCycle;
import com.openteach.diamond.repository.client.RepositoryClient;

/**
 * 
 * @author sihai
 *
 */
public class AbstractMetadataService extends AbstractLifeCycle {

	/**
	 * 
	 */
	protected RepositoryClient repositoryClient;
	
	/**
	 * 
	 * @param repositoryClient
	 */
	public AbstractMetadataService(RepositoryClient repositoryClient) {
		this.repositoryClient = repositoryClient;
	}
	
	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}
