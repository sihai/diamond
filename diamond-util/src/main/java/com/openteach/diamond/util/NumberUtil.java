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
package com.galaxy.diamond.util;

import java.util.List;

public class NumberUtil {

    public static int getMaxGYS(List<Integer> values){
        int len = values.size();
       if(len <= 1){
           return 1;
       }else if(len == 2){
           return getMaxGYS(values.get(0),values.get(1));
       }else {
           int v1 = values.get(0);
           int v2 = values.get(1);
           int temp = getMaxGYS(v1, v2);
           for(int i = 2;i<len;i++){
               temp = getMaxGYS(temp,values.get(i));
           }
           return temp;
       }
    }

    public static int getMaxGYS(int a, int b){
        while((a%=b)!=0&&(b%=a)!=0); 
        return a>b ? a: b;
    }
}
