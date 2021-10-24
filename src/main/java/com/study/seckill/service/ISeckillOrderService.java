package com.study.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.seckill.pojo.SeckillOrder;
import com.study.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 阿杰
 * @since 2021-10-09
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId -1 表示秒杀失败，0 表示排队中
     */
    Long getResult(User user, long goodsId);
}
