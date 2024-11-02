package com.example.service;


import com.example.entity.User;

public class UserServiceImplCglib {

    final void addUser(User user) {
        System.out.println("cglib...正在注册用户，用户信息为："+user);
    }
}
