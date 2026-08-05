package com.example.javaaistudent.controller;

import com.example.javaaistudent.service.TicketService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 智能票务 Controller（退票 + 买票 + 客服）。
 * <p>
 * LLM 根据用户意图自动分流：
 * <ol>
 *   <li>先用 ChatClient 结构化输出判断是否含手机号+姓名</li>
 *   <li>没有 → 提示用户补充</li>
 *   <li>有 → LLM 自动判断退票/买票/正常对话</li>
 * </ol>
 * <p>
 * Tool 由 {@code ChatClient.Builder.defaultTools(ticketService)} 一行注册。
 *
 * GET http://localhost:18080/api/ticket/chat?message=我要退票，手机号13800138000，姓名张三
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/api/ticket")
public class TicketController {



    private final ChatClient chatClient;

    public TicketController(ChatClient.Builder chatClientBuilder,
                            TicketService ticketService) {

        this.chatClient = chatClientBuilder
                .defaultTools(ticketService)
                .build();
    }

    /**
     * 票务客服入口
     */
    @GetMapping("/chat")
    public String chat(@RequestParam(defaultValue = "我要退票，手机号13800138000，姓名张三") String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}