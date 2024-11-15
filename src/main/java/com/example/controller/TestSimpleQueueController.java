package com.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Result;
import com.example.service.TestSimpleQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * rabbitmq 队列发送接口类
 */
@RestController
@RequestMapping("/testSimpleQueue")
@Slf4j
public class TestSimpleQueueController {

    @Resource
    private TestSimpleQueueService testSimpleQueueService;

    /**
     * 队列测试消费者
     * @param jsonObject
     * @return
     */
    @PostMapping("/queueTestXF")
    public Result queueTestXF(@RequestBody JSONObject jsonObject){

        return testSimpleQueueService.queueTest(jsonObject);
    }
}
