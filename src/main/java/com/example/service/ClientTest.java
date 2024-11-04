package com.example.service;

import com.example.entity.User;
import com.example.service.impl.UserServiceImpl;
import com.example.service.impl.UserServiceInterceptor;
import net.sf.cglib.proxy.Enhancer;
import org.junit.Test;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.stereotype.Service;

@Service
public class ClientTest {

    /**
     *JDK实现的动态代理
     */
//    @Test
    public void JdkTest(){
        User user = new User();
//        user.setName("hongtaolong");
//        user.setPassword("hong");
//        user.setAge(23);
        //被代理类
        UserService delegate = new UserServiceImpl();
        InvocationHandler userServiceInterceptor = new UserServiceInterceptor(delegate);
        //动态代理类
        UserService userServiceProxy = (UserService) Proxy.newProxyInstance(delegate.getClass().getClassLoader(),
                delegate.getClass().getInterfaces(), userServiceInterceptor);
        System.out.println("动态代理类："+userServiceProxy.getClass());
        userServiceProxy.addUser(user);
    }

    /**
     * CGLIB动态代理
     */
//    @Test
    public void InvocationHandlerTest(){
        User user = new User();
//        user.setName("hongtaolong");
//        user.setPassword("hong");
//        user.setAge(23);
        //被代理的对象
        UserServiceImplCglib delegate = new UserServiceImplCglib();
        UserServiceCglibInterceptor serviceInterceptor = new UserServiceCglibInterceptor(delegate);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(delegate.getClass());
        enhancer.setCallback(serviceInterceptor);
        //动态代理类
        UserServiceImplCglib cglibProxy = (UserServiceImplCglib)enhancer.create();
        System.out.println("动态代理类父类："+cglibProxy.getClass().getSuperclass());
        cglibProxy.addUser(user);
    }
}
