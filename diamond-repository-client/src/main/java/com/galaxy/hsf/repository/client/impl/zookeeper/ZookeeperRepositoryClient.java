/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.repository.client.impl.zookeeper;

import com.galaxy.hsf.repository.client.AbstractRepositoryClient;
import com.galaxy.hsf.repository.client.Certificate;
import com.galaxy.hsf.repository.client.Data;
import com.galaxy.hsf.repository.client.Key;
import com.galaxy.hsf.repository.client.cache.Cache;

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
