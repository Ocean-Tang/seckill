package com.study.seckill.rabbitmq;

import com.study.seckill.config.RabbitMQTopicConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 黄灿杰
 * @date 2021/10/16
 */
@Service
@Slf4j
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送秒杀信息
     * @param message
     */
    public void sendSeckillMessage(String message) {
      log.info("发送消息：" + message);
      rabbitTemplate.convertAndSend(RabbitMQTopicConfig.EXCHANGE,"seckill.message" ,
              message);

    }

}
