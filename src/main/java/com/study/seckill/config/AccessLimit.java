package com.study.seckill.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解
 * Retention 注解     注解生效时间
 * Target 注解        注解作用范围
 * @author 黄灿杰
 * @date 2021/10/21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {

    /**
     * 接口限流 时间间隔
     * @return
     */
    int second();

    /**
     * 接口限流，每个时间间隔限制访问的次数
     * @return
     */
    int maxCount();

    /**
     * 是否需要登录
     * @return
     */
    boolean needLogin() default true;

}
