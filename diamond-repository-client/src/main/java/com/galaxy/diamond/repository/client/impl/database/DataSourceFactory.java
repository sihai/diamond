/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.repository.client.impl.database;

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
