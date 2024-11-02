package com.example.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 新增交换机和队列
 */
@Configuration
public class FanoutConfig {

    @Bean
    public FanoutExchange fanoutExchange() {
        //ExchangeBuilder.topicExchange("topic-exchange").build();
        return new FanoutExchange("fanout-exchange");
    }

    @Bean
    public Queue queue() {
        //QueueBuilder.durable("fanout-queue");
        return new Queue("fanout-queue");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(fanoutExchange());
    }

    /**
     * 消息转换器 -- 生产者 消费者都需要
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    /**
     * 声明FanoutExchange交换机
     * @return
     */
    @Bean
    public FanoutExchange fanoutExchange1() {
        return new FanoutExchange("fanout-exchange1");
    }

    /**
     * 声明队列1
     * @return
     */
    @Bean
    public Queue queue1() {
        return new Queue("fanout-queue1");
    }

    @Bean
    public Binding binding1() {
        return BindingBuilder.bind(queue1()).to(fanoutExchange1());
    }


}
