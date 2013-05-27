/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.common.callback;


/**
 * 
 * @author sihai
 *
 */
public class CallbackHolder {

	private static Callback callback;

	public static Callback getCallback() {
		return callback;
	}

	public static void setCallback(Callback callback) {
		CallbackHolder.callback = callback;
	}
}