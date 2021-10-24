package com.study.seckill.controller;


import com.study.seckill.exception.GlobalException;
import com.study.seckill.pojo.User;
import com.study.seckill.service.impl.OrderServiceImpl;
import com.study.seckill.vo.OrderDetailVo;
import com.study.seckill.vo.RespBean;
import com.study.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 阿杰
 * @since 2021-10-09
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderServiceImpl orderService;

    @RequestMapping("/detail")
    @ResponseBody
    public RespBean orderDetail(User user, Long orderId) {
        if(user == null) {
            throw new GlobalException(RespBeanEnum.SESSION_ERROR);
        }

        OrderDetailVo orderDetailVo = orderService.getOrder(orderId);
        return RespBean.success(orderDetailVo);
    }

}
