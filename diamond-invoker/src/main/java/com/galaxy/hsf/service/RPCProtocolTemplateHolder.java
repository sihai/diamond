/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service;

import com.galaxy.hsf.rpc.RPCProtocolTemplate;

/**
 * 
 * @author sihai
 *
 */
public class RPCProtocolTemplateHolder {

	private static RPCProtocolTemplate template = null;

	public static RPCProtocolTemplate get() {
		return template;
	}

	public static void set(RPCProtocolTemplate templateService) {
		RPCProtocolTemplateHolder.template = templateService;
	}
	
	public static void unset(RPCProtocolTemplate templateService) {
		if(RPCProtocolTemplateHolder.template == templateService){
			RPCProtocolTemplateHolder.template = null;
		}
	}
}
