package com.example.service;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Result;
import org.junit.Test;

public interface TestSimpleQueueService {
    public Result queueTest(JSONObject jsonObject);
}
