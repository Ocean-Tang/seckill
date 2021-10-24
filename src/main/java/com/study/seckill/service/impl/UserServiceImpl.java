package com.study.seckill.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.seckill.exception.GlobalException;
import com.study.seckill.mapper.UserMapper;
import com.study.seckill.pojo.User;
import com.study.seckill.service.IUserService;
import com.study.seckill.utils.CookieUtil;
import com.study.seckill.utils.Md5Util;
import com.study.seckill.utils.UUIDUtil;
import com.study.seckill.vo.LoginVo;
import com.study.seckill.vo.RespBean;
import com.study.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author 黄灿杰
 * @date 2021/10/7
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录功能业务逻辑
     * @param loginVo
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response)  {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        User user = userMapper.selectById(mobile);
        if (null == user) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        // 如果密码错误
        if(!Md5Util.formPassToDbPass(password, user.getSlat())
                .equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        // 保存用户登录状态到 cookie中
        String ticket = UUIDUtil.uuid();

        redisTemplate.opsForValue().set("user:" + ticket, user);

        CookieUtil.setCookie(request, response, "userTicket", ticket);

        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        User user = null;

        if( !StringUtils.hasText(userTicket)) {
            return user;
        }

        user = (User)redisTemplate.opsForValue().get("user:" + userTicket);

        if(null != user) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }

        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket,String password, HttpServletRequest request, HttpServletResponse response) {

        User user = getUserByCookie(userTicket, request, response);


        if(user == null) {
            // 获取不到用户信息
            throw new GlobalException(RespBeanEnum.ERROR);
        }

        user.setPassword(Md5Util.formPassToDbPass(password, user.getSlat()));
        int result = userMapper.updateById(user);
        if (result == 1) {
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }

        // 更新密码失败
        throw new GlobalException(RespBeanEnum.ERROR);
    }
}
