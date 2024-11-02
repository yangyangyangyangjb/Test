package com.example.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Result;
import com.example.service.SpringRabbitListener;
import com.example.service.TestSimpleQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class TestSimpleQueueServiceImpl implements TestSimpleQueueService {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private SpringRabbitListener springRabbitListener;

    @Override
    public Result queueTest(JSONObject jsonObject) {



        return Result.ok(jsonObject);
    }
}
