package com.study.seckill.controller;

import com.study.seckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 黄灿杰
 * @date 2021/10/6
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    IUserService userService;

    @RequestMapping("/hello")
    public String test(Model model) {

        return "hello";
    }



}
