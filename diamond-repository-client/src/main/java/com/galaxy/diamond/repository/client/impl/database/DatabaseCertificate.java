/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.repository.client.impl.database;

import com.galaxy.diamond.repository.client.Certificate;

/**
 * 
 * @author sihai
 *
 */
public class DatabaseCertificate extends Certificate implements java.lang.Cloneable {

	public static final int DEFAULT_MAX_ACTIVE = 16;
	public static final int DEFAULT_MAX_IDLE = 2;
	public static final int DEFAULT_MAX_WAIT = 30000;
	public static final int DEFAULT_MIN_IDLE = 0;
	
	private String username;
	
	private String password;
	
	private String url;
	
	private String driverClassName;
	
	private int maxActive = DEFAULT_MAX_ACTIVE;
	
	private int maxIdle = DEFAULT_MAX_IDLE;
	
	private long maxWait = DEFAULT_MAX_WAIT;
	
	private int minIdle = DEFAULT_MIN_IDLE;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public long getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}
	
	@Override
	protected DatabaseCertificate clone() throws CloneNotSupportedException {
		DatabaseCertificate certificate = (DatabaseCertificate)super.clone();
		certificate.username = username;
		certificate.password = password;
		certificate.url = url;
		certificate.driverClassName = driverClassName;
		certificate.maxActive = maxActive;
		certificate.maxIdle = maxIdle;
		certificate.maxWait = maxWait;
		certificate.minIdle = minIdle;
		return certificate;
	}
}
