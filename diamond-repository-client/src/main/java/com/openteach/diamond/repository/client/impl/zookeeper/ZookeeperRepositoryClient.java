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
 * 
 */
package com.openteach.diamond.repository.client.impl.zookeeper;

import java.util.List;

import com.openteach.diamond.repository.client.AbstractRepositoryClient;
import com.openteach.diamond.repository.client.Certificate;
import com.openteach.diamond.repository.client.Data;
import com.openteach.diamond.repository.client.Key;
import com.openteach.diamond.repository.client.cache.Cache;

/**
 * 
 * @author sihai
 *
 */
public class ZookeeperRepositoryClient extends AbstractRepositoryClient {

	private final ZookeeperClient zookeeperClient;
	
	/**
	 * 
	 * @param dataSource
	 */
	public ZookeeperRepositoryClient(Certificate certificate, ZookeeperClient zookeeperClient) {
		super(certificate);
		this.zookeeperClient = zookeeperClient;
	}
	
	public ZookeeperRepositoryClient(Certificate certificate, Cache cache, ZookeeperClient zookeeperClient) {
		super(certificate, cache);
		this.zookeeperClient = zookeeperClient;
	}
	
	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		super.initialize();
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		super.start();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		super.stop();
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	protected Data getFromServer(Key key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected List<Data> mgetFromServer(Key key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void put2Server(Data data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void deleteFromServer(Key key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void monitor(Data data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void unmonitor(Data data) {
		// TODO Auto-generated method stub
		
	}
}
