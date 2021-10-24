package com.study.seckill.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.seckill.pojo.User;
import com.study.seckill.service.IUserService;
import com.study.seckill.utils.CookieUtil;
import com.study.seckill.vo.RespBean;
import com.study.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流拦截器，请求处理之前拦截
 * @author 黄灿杰
 * @date 2021/10/21
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 如果拦截对象是方法
        if (handler instanceof HandlerMethod) {
            User user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            // 判断是否对接口限流
            if (null == accessLimit) {
                return true;
            }

            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            String key = request.getRequestURI();

            if(needLogin) {
                if (null == user) {
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }

                key += ":" + user.getId();

            }

            // 接口限流 -> 这里应该有个问题：限流是全局的，而不是真的部分用户的
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get(key);
            if (null == count) {
               valueOperations.set(key, 1, second, TimeUnit.SECONDS);
            }else if (count < maxCount) {
                valueOperations.increment(key);
            }else {
                render(response, RespBeanEnum.ACCESS_LIMIT_REACHED);
                return false;
            }

        }

        return true;
    }

    /**
     * 构建返回对象
     * @param response
     * @param respBeanEnum
     * @throws IOException
     */
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        RespBean respBean = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(respBean));
        out.flush();
        out.close();

    }


    /**
     * 获取登录用户信息
     * @param request
     * @param response
     * @return
     */
    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        if(!StringUtils.hasText(userTicket)) {
            return null;
        }
        return userService.getUserByCookie(userTicket, request, response);
    }
}
