package com.study.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 黄灿杰
 * @date 2021/10/16
 */
@Configuration
public class RabbitMQTopicConfig {

    public static final String QUEUE = "seckillQueue";
    public static final String EXCHANGE = "seckillExchange";
    public static final String BINDING_KEY = "seckill.#";

    @Bean
    public Queue seckillQueue() {
        return new Queue(QUEUE, false);
    }

    @Bean
    public TopicExchange seckillExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(seckillQueue()).to(seckillExchange()).with(BINDING_KEY);
    }

}
