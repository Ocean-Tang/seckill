package com.study.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.seckill.pojo.User;
import com.study.seckill.vo.LoginVo;
import com.study.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 黄灿杰
 * @date 2021/10/7
 */
public interface IUserService extends IService<User> {
    /**
     * 执行登录功能
     * @param loginVo
     * @param request
     * @param response
     * @return
     */
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据 cookie 获取用户
     * @param userTicket
     * @param request
     * @param response
     * @return
     */
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    /**
     * 更新密码
     * 更新完毕后会 要求用户重新登陆
     * @param userTicket
     * @param password
     * @param request
     * @param response
     * @return
     */
    RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);
}
