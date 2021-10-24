package com.study.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.seckill.exception.GlobalException;
import com.study.seckill.mapper.OrderMapper;
import com.study.seckill.mapper.SeckillOrderMapper;
import com.study.seckill.pojo.Order;
import com.study.seckill.pojo.SeckillGoods;
import com.study.seckill.pojo.SeckillOrder;
import com.study.seckill.pojo.User;
import com.study.seckill.service.IOrderService;
import com.study.seckill.service.ISeckillGoodsService;
import com.study.seckill.service.ISeckillOrderService;
import com.study.seckill.utils.Md5Util;
import com.study.seckill.utils.UUIDUtil;
import com.study.seckill.vo.GoodsVo;
import com.study.seckill.vo.OrderDetailVo;
import com.study.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 阿杰
 * @since 2021-10-09
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private GoodsServiceImpl goodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ISeckillOrderService seckillOrderService;
    /**
     * 秒杀商品
     * @param user
     * @param goods
     * @return
     */
    @Override
    @Transactional
    public Order seckill(User user, GoodsVo goods) {

        // 将秒杀商品库存数量 -1
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>()
                .eq("goods_id", goods.getId()));
        if(seckillGoods.getStockCount() <= 0) {
            redisTemplate.opsForValue().set("isStockEmpty", 0);
            return null;
        }
        boolean result = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count - 1").
                eq("goods_id", goods.getId()).gt("stock_count", 0));
        if(!result) {
            return null;
        }
        /*seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        seckillGoodsService.updateById(seckillGoods);*/

        // 创建订单
        Order order = new Order(null, Long.valueOf(user.getId()), goods.getId(), 0L,
                goods.getGoodsName(), 1, seckillGoods.getSeckillPrice(), 0,
                0, new Date(), null);
        orderMapper.insert(order);

        // 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder(null, Long.valueOf(user.getId()), order.getId(), goods.getId());
        seckillOrderService.save(seckillOrder);

        // 将订单信息缓存到Redis
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(),
                seckillOrder, 5, TimeUnit.MINUTES);

        return order;
    }

    @Override
    public OrderDetailVo getOrder(Long orderId) {

        if(orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXISTS);
        }

        Order order = orderMapper.selectById(orderId);
        if(order == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXISTS);
        }

        GoodsVo goodsVo = goodsService.findGoodsVoById(order.getGoodsId());

        return new OrderDetailVo(order, goodsVo);

    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createPath(User user, Long goodsId) {

        String path = Md5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId,
                path, 60, TimeUnit.SECONDS);
        return path;
    }

    /**
     * 检验请求接口是否合法
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    @Override
    public boolean checkPath(User user, long goodsId, String path) {

        if( null == user || goodsId < 0 || !StringUtils.hasText(path)) {
            return false;
        }

        String redisPath = (String) redisTemplate.opsForValue().
                get("seckillPath:" + user.getId() + ":" + goodsId);

        return path.equals(redisPath);
    }

    /**
     * 检验 验证码是否 正确
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {

        if (null == user || goodsId < 0 || !StringUtils.hasText(captcha)) {
            return false;
        }

        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
