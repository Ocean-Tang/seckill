package com.study.seckill.rabbitmq;

import com.study.seckill.config.RabbitMQTopicConfig;
import com.study.seckill.exception.GlobalException;
import com.study.seckill.pojo.SeckillMessage;
import com.study.seckill.pojo.SeckillOrder;
import com.study.seckill.pojo.User;
import com.study.seckill.service.IOrderService;
import com.study.seckill.service.impl.GoodsServiceImpl;
import com.study.seckill.service.impl.OrderServiceImpl;
import com.study.seckill.utils.JsonUtil;
import com.study.seckill.vo.GoodsVo;
import com.study.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息消费者
 * @author 黄灿杰
 * @date 2021/10/16
 */
@Slf4j
@Service
public class MQReceive {


    @Autowired
    private GoodsServiceImpl goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

    /**
     * 下单操作
     * @param message
     */
    @RabbitListener(queues = RabbitMQTopicConfig.QUEUE)
    public void receiveSeckillMessage(String message) {
        log.info("接受秒杀消息");
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        User user = seckillMessage.getUser();
        long goodsId = seckillMessage.getGoodsId();

        GoodsVo goods = goodsService.findGoodsVoById(goodsId);
        // 判断商品库存
        if (goods.getStockCount() < 1) {
            return;
        }
        // 判断是否重复抢购
        SeckillOrder seckillOrder =
                ((SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId));
        if(seckillOrder != null) {
            return;
        }
        // 下单操作
        orderService.seckill(user, goods);
    }

}
