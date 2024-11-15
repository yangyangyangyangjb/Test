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
     * 队列测试
     * @param jsonObject
     * @return
     */
    @PostMapping("/queueTest")
    public Result queueTest(@RequestBody JSONObject jsonObject){

        return testSimpleQueueService.queueTest(jsonObject);
    }

    /**
     * 队列测试
     * 广播路由
     * @param jsonObject
     * @return
     */
    @PostMapping("/fanout")
    public Result fanout(@RequestBody JSONObject jsonObject){

        return testSimpleQueueService.fanout(jsonObject);
    }

    /**
     * 队列测试
     * 定向路由
     * @param jsonObject
     * @return
     */
    @PostMapping("/direct")
    public Result direct(@RequestBody JSONObject jsonObject){

        return testSimpleQueueService.direct(jsonObject);
    }

    /**
     * 队列测试
     * topic
     * @param jsonObject
     * @return
     */
    @PostMapping("/topic")
    public Result topic(@RequestBody JSONObject jsonObject){

        return testSimpleQueueService.topic(jsonObject);
    }
}
