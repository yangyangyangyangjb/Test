package com.example.service;

import com.example.entity.User;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class UserServiceCglibInterceptor implements MethodInterceptor {

    private Object realObject;

    public UserServiceCglibInterceptor(Object realObject) {
        super();
        this.realObject = realObject;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("intercept方法进入...");
        if (args!=null && args.length > 0 && args[0] instanceof User) {
            User user = (User)args[0];
            //进行增强判断
//            if (user.getName().length() <= 1) {
//                throw new RuntimeException("用户名长度必须大于1");
//            }
//            if (user.getPassword().length() <= 6) {
//                throw new RuntimeException("密码长度必须大于6");
//            }
        }
        Object result = method.invoke(realObject, args);
        System.out.println("用户注册成功...");
        return result;
    }

}
