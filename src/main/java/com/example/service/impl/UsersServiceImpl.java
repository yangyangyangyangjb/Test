package com.example.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Result;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result getUser(JSONObject jsonObject) {
        List<User> list = new ArrayList<>();
        try{
            list =  userMapper.getAll();
        }catch (Exception e){
            log.error("获取用户信息失败", e);
        }

        return Result.ok(list);
    }
}
