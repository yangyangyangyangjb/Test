package com.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Result;
import lombok.extern.slf4j.Slf4j;
import com.example.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Slf4j
public class UsersController {
    @Autowired
    private UsersService usersService;

    @PostMapping("/getUser")
    public Result getUser(@RequestBody JSONObject jsonObject){

        return usersService.getUser(jsonObject);
    }
}
