package com.example.service;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Result;

public interface DepositProductService {
    /**
     * 存款产品同步发送财司
     */
    public Result sendDepositProductToCs(JSONObject jsonObject);

}
