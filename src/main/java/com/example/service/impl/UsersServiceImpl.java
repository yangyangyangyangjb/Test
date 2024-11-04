package com.example.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Result;
import com.example.entity.User;
//import com.example.mapper.UserMapper;
import com.example.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UsersServiceImpl implements UsersService {

//    @Resource
//    private UserMapper userMapper;

    @Override
    public Result getUser(JSONObject jsonObject) {
        List<User> list = new ArrayList<>();
        try{
//            list =  userMapper.getAll();
            log.info("list:{}", list);
        }catch (Exception e){
            log.error("获取用户信息失败", e);
        }

        return Result.ok(list);
    }
}
