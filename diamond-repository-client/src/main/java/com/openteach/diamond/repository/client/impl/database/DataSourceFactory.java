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

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * 
 * @author sihai
 *
 */
public class DataSourceFactory {
	
	private DatabaseCertificate certificate;
	
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	public DataSourceFactory withCertificate(DatabaseCertificate certificate) {
		this.certificate = certificate;
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public DataSource newInstance() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDefaultAutoCommit(true);
		dataSource.setDriverClassName(certificate.getDriverClassName());
		dataSource.setMaxActive(certificate.getMaxActive());
		dataSource.setMaxIdle(certificate.getMaxIdle());
		dataSource.setMaxWait(certificate.getMaxWait());
		dataSource.setMinIdle(certificate.getMinIdle());
		dataSource.setUsername(certificate.getUsername());
		dataSource.setPassword(certificate.getPassword());
		dataSource.setUrl(certificate.getUrl());
		return dataSource;
	}
}
