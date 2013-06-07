/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author sihai
 *
 */
public class ServiceURL {

	/**
	 * 
	 */
	private String strURL;
	
	/**
	 * 
	 */
	private String protocol;
	
	/**
	 * 
	 */
	private String host;
	
	/**
	 * 
	 */
	private int port;
	
	/**
	 * 
	 */
	private String serviceName;
	
	/**
	 * 
	 */
	private String query;
	
	/**
	 * 
	 * @param strURL
	 * @throws MalformedURLException
	 */
	public ServiceURL(String strURL) throws MalformedURLException {
		this.strURL = strURL;
		parse();
	}
	
	/**
	 * 
	 * @throws MalformedURLException
	 */
	private void parse() throws MalformedURLException {
		int index = strURL.indexOf("://");
		URL url = new URL(String.format("http%s", strURL.substring(index)));
		protocol = strURL.substring(0, index);
		host = url.getHost();
		port = url.getPort();
		serviceName = url.getFile();
		query = url.getQuery();
	}

	public String getStrURL() {
		return strURL;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getQuery() {
		return query;
	}

	@Override
	public int hashCode() {
		return strURL.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(!(obj instanceof ServiceURL)) {
			return false;
		}
		return strURL.equals(((ServiceURL)obj).strURL);
	}
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		return String.format("%s://%s:%d%s%s", protocol, host, port, serviceName, StringUtils.isBlank(query) ? "" : "?" + StringUtils.trim(query));
	}
	
}
