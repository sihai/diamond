/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.metadata;

import java.util.List;

import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.service.ConsumerHook;

/**
 * 
 * @author sihai
 *
 */
public interface Consumer {

	/**
	 * 生成调用远程HSF服务的代理<br>
	 * 此代理的效果为生成ServiceMetadata中指定的interface的代理，调用时可将代理转型为服务接口，并进行直接的对象调用<br>
	 * 代理将完成对于远程HSF的调用
	 * 
	 * @param metadata 服务模型对象
	 * 
	 * @throws HSFException 当生成调用远程HSF服务的代理失败时，抛出此异常
	 */
	Object consume(ServiceMetadata metadata) throws HSFException;
	
	
	/**
	 * 获取当前注入的hook列表
	 * 
	 * @return
	 */
	List<ConsumerHook> getHooks(); 
}
