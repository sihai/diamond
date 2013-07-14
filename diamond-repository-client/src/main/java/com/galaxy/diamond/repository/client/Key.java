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
package com.galaxy.diamond.repository.client;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * Key of data
 * @author sihai
 *
 */
public class Key implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1171314496745268310L;
	
	public static final String NAMESPACE_KEY_SEPARATOR = ":";
	public static final String ERROR_MSG = String.format("namespace or key must not contains separator of namespace and key:\"%s\"", NAMESPACE_KEY_SEPARATOR);
	
	/**
	 * 
	 */
	private String namespace;
	
	/**
	 * 
	 */
	private String key;
	
	/**
	 * 
	 */
	private long sequence = 0;
	
	
	public Key() {}
	
	public Key(String namespace, String key, long sequence) {
		if(namespace.contains(NAMESPACE_KEY_SEPARATOR) || key.contains(NAMESPACE_KEY_SEPARATOR)) {
			throw new IllegalArgumentException(ERROR_MSG);
		}
		this.namespace = namespace;
		this.key = key;
		this.sequence = sequence;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		if(namespace.contains(NAMESPACE_KEY_SEPARATOR)) {
			throw new IllegalArgumentException(ERROR_MSG);
		}
		this.namespace = namespace;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		if(key.contains(NAMESPACE_KEY_SEPARATOR)) {
			throw new IllegalArgumentException(ERROR_MSG);
		}
		this.key = key;
	}
	
	public long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}
	
	public void incSequence() {
		this.sequence += 1;
	}
	
	public String getFullKey() {
		return String.format("%s:%s", namespace, key);
	}
	
	public void setFullKey(String fullKey) {
		String[] tmp = fullKey.split(NAMESPACE_KEY_SEPARATOR);
		this.namespace = tmp[0];
		this.key = tmp[1];
	}
	
	@Override
	public int hashCode() {
		int code = 32 * namespace.hashCode() + 1;
		code += 32 * key.hashCode();
		code += sequence;
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj == this) {
			return true;
		}
		
		if(null == obj) {
			return false;
		}
		
		return StringUtils.equals(namespace, ((Key)obj).namespace) && StringUtils.equals(key, ((Key)obj).key) && sequence == ((Key)obj).sequence;
	}
}
