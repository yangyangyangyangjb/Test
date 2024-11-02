package com.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * RabbitMQ 消费者
 */
@SpringBootApplication
@ServletComponentScan //开启filter过滤器，添加该注解时@WebFilter注解才会生效
@Slf4j
public class Application {

    public static void main(String[] args){
        log.error("redisTest1\nredisTest1");
        SpringApplication.run(Application.class, args);
    }

}
