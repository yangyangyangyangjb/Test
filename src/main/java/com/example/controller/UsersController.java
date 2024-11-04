package com.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Result;
import lombok.extern.slf4j.Slf4j;
import com.example.service.UsersService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/users")
@Slf4j
public class UsersController {
    @Resource
    private UsersService usersService;

    @PostMapping("/getUser")
    public Result getUser(@RequestBody JSONObject jsonObject){

        return usersService.getUser(jsonObject);
//        return Result.ok();
    }
}
