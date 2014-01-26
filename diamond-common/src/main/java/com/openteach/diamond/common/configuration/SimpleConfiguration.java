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
 */
package com.openteach.diamond.common.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.openteach.diamond.common.Constants;

/**
 * 
 * @author sihai
 *
 */
public class SimpleConfiguration implements Configuration {

	private static final String HSF_CONFIG_PROPERTIES_FILE = "hsfconfig.properties";
    private static final String HSF_SERVER_MIN_POOLSIZE_KEY = "hsf.server.min.poolsize";
    private static final String HSF_SERVER_MAX_POOLSIZE_KEY = "hsf.server.max.poolsize";
    private static final String HSF_SERVER_PORT_KEY = "hsf.server.port";
    private static final String HSF_RUN_MODE_KEY = "hsf.run.mode";
    private static final String HSF_CLIENT_LOCALCALL = "hsf.client.localcall";

    private static int hsfServerPort = Constants.DEFAULT_SERVER_PORT;
    private static int hsfServerMaxThreadPoolSize = Constants.DEFAULT_SERVER_MAX_THREAD_POOL_SIZE;
    private static int hsfServerMinThreadPoolSize = Constants.DEFAULT_SERVER_MIN_THREAD_POOL_SIZE;
    private static int consumerProfiler = 3000;
    private static int providerProfiler = 500;
    private static int runmode = Constants.RUN_MODE_TEST;
    private static boolean localCallEnable = true; //是否开启本地调用优化
    
    static {
        Properties properties = new Properties();
        try{
            InputStream in = SimpleConfiguration.class.getClassLoader().getResourceAsStream(HSF_CONFIG_PROPERTIES_FILE);
            properties.load(in);
        } catch(IOException e){
            properties.put(HSF_RUN_MODE_KEY, String.valueOf(runmode));
            properties.put(HSF_SERVER_MAX_POOLSIZE_KEY, String.valueOf(hsfServerMaxThreadPoolSize));
            properties.put(HSF_SERVER_MIN_POOLSIZE_KEY, String.valueOf(hsfServerMinThreadPoolSize));
            properties.put(HSF_SERVER_PORT_KEY, String.valueOf(hsfServerPort));
            properties.put(HSF_CLIENT_LOCALCALL, "" + localCallEnable);
        }

        try {
			String tmp = null;
			tmp = properties.getProperty(HSF_SERVER_PORT_KEY).trim();
			hsfServerPort = Integer.parseInt(tmp);

			tmp = properties.getProperty(HSF_SERVER_MIN_POOLSIZE_KEY).trim();
			hsfServerMinThreadPoolSize = Integer.parseInt(tmp);

			tmp = properties.getProperty(HSF_SERVER_MAX_POOLSIZE_KEY).trim();
			hsfServerMaxThreadPoolSize = Integer.parseInt(tmp);

			tmp = properties.getProperty(HSF_RUN_MODE_KEY).trim();
			if (null != tmp) {
			    runmode = Integer.parseInt(tmp);
			}
			tmp = properties.getProperty(HSF_CLIENT_LOCALCALL).trim();
			localCallEnable = Boolean.parseBoolean(tmp);
		} catch (Exception e) {//
			e.printStackTrace();
		}
		// 增加HSF从系统变量里面获取端口配置,默认覆盖hsf的配置文件
		String serverPortString = System.getProperty(HSF_SERVER_PORT_KEY);
		if(serverPortString != null){
			try {
				hsfServerPort = Integer.parseInt(serverPortString.trim());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
    }
    
    public int getRunmode() {
        return runmode;
    }
    public void setRunmode(int mode) {
        runmode = mode;
    }

    public int getHSFServerPort() {
        return hsfServerPort;
    }

    public int getHSFServerMaxThreadPoolSize() {
        return hsfServerMaxThreadPoolSize;
    }
    public void setHSFServerMaxThreadPoolSize(int maxPoolSize) {
        SimpleConfiguration.hsfServerMaxThreadPoolSize = maxPoolSize;
    }
    public int getHSFServerMinThreadPoolSize() {
        return hsfServerMinThreadPoolSize;
    }
    public void setHSFServerMinThreadPoolSize(int minPoolSize) {
        SimpleConfiguration.hsfServerMinThreadPoolSize=minPoolSize;
    }

    public int getConsumerProfiler() {
        return consumerProfiler;
    }
    public void setConsumerProfiler(int consumerProfiler) {
        SimpleConfiguration.consumerProfiler=consumerProfiler;
    }
    public int getProviderProfiler() {
        return providerProfiler;
    }
    public void setProviderProfiler(int providerProfiler) {
        SimpleConfiguration.providerProfiler = providerProfiler;
    }
    public boolean isLocalCallEnable() {
        return localCallEnable;
    }
    public void setLocalCallEnable(boolean localCallEnable) {
        SimpleConfiguration.localCallEnable = localCallEnable;
    }
}