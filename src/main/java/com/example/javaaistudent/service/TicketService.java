package com.example.javaaistudent.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * 票务服务——买票 + 退票，只打日志不实现业务。
 *
 * @author Administrator
 */
@Service
public class TicketService {

    /**
     * 退票工具
     */
    @Tool(name = "退票", description = "为用户办理退票，需要手机号和姓名。必须提供这两个参数才能调用")
    public String refundTicket(
            @ToolParam(description = "用户的手机号码", required = true) String phone,
            @ToolParam(description = "用户的姓名", required = true) String name) {

        System.out.println("========== 退票 Tool 被调用 ==========");
        System.out.println("手机号: " + phone);
        System.out.println("姓  名: " + name);
        System.out.println("=====================================");

        return "退票已办理成功。手机号: " + phone + ", 姓名: " + name;
    }

    /**
     * 买票工具
     */
    @Tool(name = "买票", description = "为用户购买车票，需要手机号和姓名。必须提供这两个参数才能调用")
    public String buyTicket(
            @ToolParam(description = "用户的手机号码", required = true) String phone,
            @ToolParam(description = "用户的姓名", required = true) String name) {

        System.out.println("========== 买票 Tool 被调用 ==========");
        System.out.println("手机号: " + phone);
        System.out.println("姓  名: " + name);
        System.out.println("=====================================");

        return "购票已办理成功。手机号: " + phone + ", 姓名: " + name;
    }
}