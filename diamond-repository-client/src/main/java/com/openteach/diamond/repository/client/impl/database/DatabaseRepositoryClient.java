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
package com.openteach.diamond.repository.client.impl.database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import com.openteach.diamond.common.thread.CommonThreadFactory;
import com.openteach.diamond.repository.client.AbstractRepositoryClient;
import com.openteach.diamond.repository.client.Certificate;
import com.openteach.diamond.repository.client.Data;
import com.openteach.diamond.repository.client.DataEvent;
import com.openteach.diamond.repository.client.Key;
import com.openteach.diamond.repository.client.cache.Cache;
import com.openteach.diamond.repository.client.exception.SequenceNotMatchException;

/**
 * 
 * @author sihai
 *
 */
public class DatabaseRepositoryClient extends AbstractRepositoryClient {

	/**
	 * 15
	 */
	public static final long MONITOR_SCAN_PERIOD = 15;
	
	/**
	 * For access database
	 */
	private DataSource dataSource;
	
	/**
	 * For access database
	 */
	private DataDAO dataDAO;
	
	/**
	 * For monitor lock
	 */
	private byte[] _monitor_lock_ = new byte[0];
	
	/**
	 * Monitored data
	 */
	private List<Data> monitoredDataList = new ArrayList<Data>(8);
	
	/**
	 * Monitor thread
	 */
	private ScheduledExecutorService scheduledExecutorService;										// 
	
	/**
	 * 
	 * @param dataSource
	 */
	public DatabaseRepositoryClient(Certificate certificate, DataSource dataSource) {
		super(certificate);
		this.dataSource = dataSource;
	}
	
	public DatabaseRepositoryClient(Certificate certificate, Cache<Key, Object> cache, DataSource dataSource) {
		super(certificate, cache);
		this.dataSource = dataSource;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		dataDAO = new DataDAO(dataSource);
	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		if(null != scheduledExecutorService) {
			scheduledExecutorService.shutdownNow();
		}
		monitoredDataList.clear();
		monitoredDataList = null;
	}

	@Override
	public void destroy() {
		super.destroy();
	}
	
	@Override
	protected Data getFromServer(Key key) {
		return dataDAO.query(key);
	}
	
	@Override
	protected List<Data> mgetFromServer(Key key) {
		return dataDAO.queryList(key);
	}


	@Override
	protected void put2Server(Data data) throws SequenceNotMatchException {
		dataDAO.insert(data);
	}
	
	@Override
	protected void deleteFromServer(Key key) {
		dataDAO.delete(key);
	}

	@Override
	protected void monitor(Data data) {
		synchronized(_monitor_lock_) {
			monitoredDataList.add(data);
			if(null == scheduledExecutorService) {
				startMonitorThread();
			}
		}
	}

	@Override
	protected void unmonitor(Data data) {
		synchronized(_monitor_lock_) {
			monitoredDataList.remove(data);
			if(monitoredDataList.isEmpty()) {
				// XXX Stop monitor thread now or at the stop of client
				// Maybe at the stop of client is better
			}
		}
	}

	/**
	 * Start monitor thread
	 */
	private void startMonitorThread() {
		this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new CommonThreadFactory("HSF-Repository-Client-Monitor", null, true));
		this.scheduledExecutorService.scheduleAtFixedRate(new MonitorTask(), MONITOR_SCAN_PERIOD, MONITOR_SCAN_PERIOD, TimeUnit.SECONDS);
	}
	
	/**
	 * 
	 */
	private void scan() {
		synchronized(_monitor_lock_) {
			for(Data oldData : monitoredDataList) {
				Data newData = dataDAO.query(oldData.getKey());
				if(null == newData) {
					// Deleted
					fire(new DataEvent.DeletedDataEventBuilder().withOldData(oldData).build());
					monitoredDataList.remove(oldData);
				} else if(!newData.equals(oldData)) {
					// Modified
					fire(new DataEvent.ModifiedDataEventBuilder().withOldData(oldData).withNewData(newData).build());
					monitoredDataList.remove(oldData);
					monitoredDataList.add(newData);
				}
			}
		}
	}
	
	private class MonitorTask implements Runnable {

		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()) {
				scan();
			}
		}
	}
}
