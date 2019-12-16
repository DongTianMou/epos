package com.dongtian.epos.utils;

import java.util.Random;

public class GetIDUtils {
    /**
     * 生成唯一的主键,并加上synchronize关键字，防止多线程下重复
     * 格式: 时间+随机数
     * @return
     */
    public static synchronized String getUniqueId(){
        Random random = new Random( );
        //随机生成一个整数,范围就变成[0,a)。
        Integer number = random.nextInt(900000) + 100000;
        return System.currentTimeMillis() + String.valueOf(number);
    }
}
