/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.common.log;

import java.io.File;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.xml.DOMConfigurator;

import com.galaxy.hsf.common.HSFConstants;

/**
 * 
 * @author sihai
 *
 */
public class Logger {

	public static final String LOGGER_NAME = "com.galaxy.hsf";
	
	private static final Log LOGGER = LogFactory.getLog(LOGGER_NAME);
	private static AtomicBoolean initialized = new AtomicBoolean(false);	//
	private static final String version = HSFConstants.CURRENT_VERSION;

	static public void init() {
		if(!initialized.compareAndSet(false, true)) {
    		return;
    	}
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(Logger.class.getClassLoader());
		// 使HSF的log4j配置生效(Logger, Appender)
		DOMConfigurator.configure(Logger.class.getClassLoader().getResource("hsf_log4j_" + version + ".xml"));

		// 设置log4j.xml中所有的Appender, 将这些appender输出的日志全部进行改变
		String bizLogDir = System.getProperty("HSF.LOG.PATH");
		if ((bizLogDir == null) || ("".equals(bizLogDir.trim()))) {
			Thread.currentThread().setContextClassLoader(loader);
			return;
		}
		FileAppender fileAppender = null;
		for (Enumeration<?> appenders = org.apache.log4j.Logger.getRootLogger().getAllAppenders(); (null == fileAppender) && appenders.hasMoreElements();) {
			Appender appender = (Appender) appenders.nextElement();
			if (FileAppender.class.isInstance(appender)) {
				FileAppender logFileAppender=(FileAppender)appender;
				File deleteFile=new File(logFileAppender.getFile());
				File logFile = new File(bizLogDir, logFileAppender.getFile());
				logFileAppender.setFile(logFile.getAbsolutePath());
				logFileAppender.activateOptions(); // 很重要，否则原有日志内容会被清空
				if(deleteFile.exists()){
					deleteFile.delete();
				}
				LOGGER.warn("成功改变" + logFileAppender.getFile() + "的输出路径:" + logFile.getAbsolutePath());
				
			}
		}
		Thread.currentThread().setContextClassLoader(loader);
	}
}