/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.common.callback;

import com.galaxy.hsf.common.exception.HSFException;

/**
 * 
 * @author sihai
 *
 */
public interface Callback {

	/**
	 * 由于装载Container类的问题，这里只能是采用Object的方式了
	 */
	void setHSFContainer(Object container);

	/**
	 * 回调下HSFContainer
	 */
	void init() throws HSFException;
}