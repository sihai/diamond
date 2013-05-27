/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.util;

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
