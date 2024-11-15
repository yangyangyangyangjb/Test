package com.example.service;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Result;
import org.junit.Test;

public interface TestSimpleQueueService {
    Result queueTest(JSONObject jsonObject);
}
