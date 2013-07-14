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
package com.galaxy.diamond.router;

/**
 * 一个表示路由规则的Bean<p>
 * 一个服务的路由规则<p>
 * 
 * 具体规则的表现形式为一个地址过滤正则式列表,
 * 语义为：若一个地址满足列表中的某一个正则式，则会被选中，否则被过滤,
 * 被选中的地址会被加入到最终调用列表中.
 * 
 * @param <M> 代表方法签名的对象。
 *            可以是java.lang.reflect.Method，也可以是一个方法名和参数类型连接起来的String
 *            如果是后者，连接符必须为RouteRule.METHOD_SIGS_JOINT_MARK
 *            连接的第一项为方法名，之后各项为每个参数的类名称(Class.getName()返回值)。
 *            
 * @author sihai
 *
 */
public class Rule {

}
