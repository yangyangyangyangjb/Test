package com.example.service;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Result;

public interface TestSimpleQueueService {

    Result queueTest(JSONObject jsonObject);
    Result fanout(JSONObject jsonObject);
    Result direct(JSONObject jsonObject);
    Result topic(JSONObject jsonObject);
}
