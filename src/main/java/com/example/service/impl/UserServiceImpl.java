package com.example.service.impl;

import com.example.entity.User;
import com.example.service.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public void addUser(User user) {
        System.out.println("jdk...正在注册用户，用户信息为："+user);
    }

}
