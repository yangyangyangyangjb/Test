package com.example.service.impl;

import com.example.entity.User;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

public class UserServiceInterceptor implements InvocationHandler {

    private Object realObj;

    public UserServiceInterceptor(Object realObject) {
        super();
        this.realObj = realObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (args!=null && args.length > 0 && args[0] instanceof User) {
            User user = (User)args[0];
            //进行增强判断
            /*if (user.getName().length() <= 1) {
                throw new RuntimeException("用户名长度必须大于1");
            }
            if (user.getPassword().length() <= 6) {
                throw new RuntimeException("密码长度必须大于6");
            }*/
        }
        Object result = method.invoke(realObj, args);
        System.out.println("用户注册成功...");
        return result;
    }

    public Object getRealObj() {
        return realObj;
    }

    public void setRealObj(Object realObj) {
        this.realObj = realObj;
    }

}