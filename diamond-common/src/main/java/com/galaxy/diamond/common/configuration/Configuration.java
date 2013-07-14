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
 */
package com.galaxy.diamond.common.configuration;

/**
 * 
 * @author sihai
 *
 */
public interface Configuration {

	/**
     * HSF SocketServer的端口。只读。
     */
    int getHSFServerPort();

    /**
     * HSF处理端线程池大小
     */
    void setHSFServerMinThreadPoolSize(int minPoolSize);
    int getHSFServerMinThreadPoolSize();
    void setHSFServerMaxThreadPoolSize(int maxPoolSize);
    int getHSFServerMaxThreadPoolSize();

    /**
     * 客户端等待服务响应时间阀值
     */
    void setConsumerProfiler(int consumerProfiler);
    int getConsumerProfiler();

    /**
     * 服务端处理业务时间阀值
     */
    void setProviderProfiler(int providerProfiler);
    int getProviderProfiler();

    /**
     * HSF运行模式，0代表测试环境，1代表正式环境
     */
    public void setRunmode(int mode);
    public int getRunmode();

    /**
     * 是否启动本地调用优化
     */
    boolean isLocalCallEnable();
    void setLocalCallEnable(boolean localCallEnable);
}