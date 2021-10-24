package com.study.seckill.utils;

import java.util.UUID;

/**
 * UUID 工具类
 * @author 黄灿杰
 * @date 2021/10/7
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
