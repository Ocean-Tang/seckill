package com.study.seckill.controller;

import com.study.seckill.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 黄灿杰
 * @date 2021/10/10
 */
@Controller
@RequestMapping("/user")
public class UserController {


    @RequestMapping("/info")
    @ResponseBody
    public User returnUser(User user) {
        return user;
    }

}
