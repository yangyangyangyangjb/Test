package com.example.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Result;
import com.example.service.TestSimpleQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * rabbitmq 简单队列实现类
 */
@Service
@Slf4j
public class TestSimpleQueueServiceImpl implements TestSimpleQueueService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 生产者 consumer
     * @param jsonObject
     * @return
     */
    @Override
    public Result queueTest(JSONObject jsonObject) {

        if(false){
            //队列名称
            String queueName = "simple.queue";

            //消息
            String message = jsonObject.toJSONString();

            //发送消息
            rabbitTemplate.convertAndSend(queueName, message);
        }

        for(int i = 0;i<50;i++){
            //队列名称
            String queueName = "work.queue";

            //消息
            int message = i;

            //发送消息
            rabbitTemplate.convertAndSend(queueName, message);
        }

        return Result.ok();
    }


    /**
     * 生产者 consumer
     * 广播路由
     * @param jsonObject
     * @return
     */
    @Override
    public Result fanout(JSONObject jsonObject) {

        if(false){
            //队列名称
            String exchangName = "hmall.fanout";

            //消息
            String message = jsonObject.toJSONString();

            //发送消息
            rabbitTemplate.convertAndSend(exchangName,"", message);
        }

        if(true){
            for(int i = 0;i<50;i++){
                //队列名称
                String exchangName = "hmall.fanout";

                //消息
                int message = i;

                //发送消息
                rabbitTemplate.convertAndSend(exchangName,"", message);
            }
        }


        return Result.ok();
    }

    /**
     * 生产者 consumer
     * 定向路由
     * @param jsonObject
     * @return
     */
    @Override
    public Result direct(JSONObject jsonObject) {

        if(true){
            //队列名称
            String exchangName = "hmall.direct";

            String a = jsonObject.getString("a");
            //消息
            String message = jsonObject.toJSONString();

            if(Objects.equals(a,"1")){
                rabbitTemplate.convertAndSend(exchangName, "queue1", message);
            } else if (Objects.equals(a,"2")) {
                //发送消息
                rabbitTemplate.convertAndSend(exchangName,"queue2", message);
            }
        }

        if(false){
            for(int i = 0;i<50;i++){
                //队列名称
                String exchangName = "hmall.fanout";

                //消息
                int message = i;

                //发送消息
                rabbitTemplate.convertAndSend(exchangName,"", message);
            }
        }


        return Result.ok();
    }

    /**
     * 生产者 consumer
     * topic
     * @param jsonObject
     * @return
     */
    @Override
    public Result topic(JSONObject jsonObject) {

        if(true){
            //队列名称
            String exchangName = "cs.topic";

            String a = jsonObject.getString("a");
            //消息
            String message = jsonObject.toJSONString();

            if(Objects.equals(a,"1")){
                rabbitTemplate.convertAndSend(exchangName, "shanDong.jinan", message);
            } else if (Objects.equals(a,"2")) {
                //发送消息
                rabbitTemplate.convertAndSend(exchangName,"shanDong.zaoZhuang", message);
            }
        }

        if(false){
            for(int i = 0;i<50;i++){
                //队列名称
                String exchangName = "hmall.fanout";

                //消息
                int message = i;

                //发送消息
                rabbitTemplate.convertAndSend(exchangName,"", message);
            }
        }


        return Result.ok();
    }
}
