package com.study.seckill.vo;

import lombok.*;

/**
 * @author 黄灿杰
 * @date 2021/10/7
 */
@Getter
@AllArgsConstructor
@ToString
public enum RespBeanEnum {

    // 成功返回
    SUCCESS(200, "SUCCESS"),
    // 失败返回
    ERROR(500, "服务端错误"),
    // 登录验证
    LOGIN_ERROR(501, "用户名或密码错误"),
    // 参数校验错误
    BIND_ERROR(502, "参数校验错误"),
    // 空库存错误
    EMPTY_STOCK(505, "商品没有库存了"),
    // 秒杀重复下单错误
    REPEATE_ERROR(506, "用户不能重复秒杀同样的商品"),
    // 用户不存在错误
    SESSION_ERROR(507, "用户不存在"),
    // 订单不存在错误
    ORDER_NOT_EXISTS(508, "订单不存在"),
    // 接口错误
    REQUEST_ILLEGAL(509, "请求非法"),
    // 验证码错误
    CAPTCHA_ERROR(510, "验证码错误"),
    // 请求超过限制
    ACCESS_LIMIT_REACHED(511, "请求超过限制")
    ;
    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 返回信息
     */
    private final String message;
}
