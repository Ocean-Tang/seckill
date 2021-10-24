package com.study.seckill.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * @author 黄灿杰
 * @date 2021/10/7
 */
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
public class User {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，手机号码
     */
    private String id;

    private String nickname;

    /**
     * MD5(MD5(pass明文 +固定salt)+ salt)
     */
    private String password;

    private String slat;

    /**
     * 头像
     */
    private String head;

    /**
     * 注册时间
     */
    private Date registerDate;

    /**
     * 最后一次登录时间
     */
    private Date lastLoginDate;

    /**
     * 登录次数
     */
    private Integer loginCount;

}
