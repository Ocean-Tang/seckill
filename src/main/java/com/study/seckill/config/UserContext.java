package com.study.seckill.config;

import com.study.seckill.pojo.User;

/**
 * 用于存放每个用户线程的 用户信息
 * @author 黄灿杰
 * @date 2021/10/21
 */
public class UserContext {

    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void setUser(User user) {
        userHolder.set(user);
    }

    public static User getUser() {
        return userHolder.get();
    }

}
