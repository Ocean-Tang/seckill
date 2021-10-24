package com.study.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.seckill.pojo.Order;
import com.study.seckill.pojo.User;
import com.study.seckill.vo.GoodsVo;
import com.study.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 阿杰
 * @since 2021-10-09
 */
public interface IOrderService extends IService<Order> {

    /**
     * 秒杀商品
     * @param user
     * @param goods
     * @return
     */
    Order seckill(User user, GoodsVo goods);

    /**
     * 获取订单详情
     * @param orderId
     * @return
     */
    OrderDetailVo getOrder(Long orderId);

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    String createPath(User user, Long goodsId);

    /**
     * 检查接口是否合法
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    boolean checkPath(User user, long goodsId, String path);

    /**
     * 检验验证码是否正确
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
