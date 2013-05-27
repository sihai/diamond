package com.galaxy.hsf.network.waverider;

import java.io.Serializable;

/**
 * 
 * @author raoqiang
 *
 */
public class State implements Serializable{
	
	private static final long serialVersionUID = -392821401628214609L;
	
	protected Long id;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
