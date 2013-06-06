/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.repository.client;

/**
 * Certificate of access repository server
 * @author sihai
 *
 */
public class Certificate implements java.lang.Cloneable {

	/**
	 * Name allocated for this app
	 */
	private String appName;
	
	/**
	 * Secret for this appName
	 */
	private String secret;
	
	/**
	 * Namespace allocated for this app
	 */
	private String namespace;
	
	// TODO privileges readonly, writeable, or ACL
	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	@Override
	protected Certificate clone() throws CloneNotSupportedException {
		Certificate certificate = (Certificate)super.clone();
		certificate.appName = appName;
		certificate.secret = secret;
		return certificate;
	}
}
