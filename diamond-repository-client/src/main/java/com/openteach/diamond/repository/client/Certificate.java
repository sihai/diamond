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
 * 
 */
package com.openteach.diamond.repository.client;

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
