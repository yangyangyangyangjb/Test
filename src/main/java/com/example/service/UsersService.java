package com.example.service;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Result;

public interface UsersService {

    Result getUser(JSONObject jsonObject);
}
