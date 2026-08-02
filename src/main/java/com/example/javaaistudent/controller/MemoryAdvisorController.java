package com.example.javaaistudent.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 多轮对话记忆 Controller（Advisor 拦截器版）
 * 通过 PromptChatMemoryAdvisor 自动读写 ChatMemory，
 * 每次 chatClient.prompt().user(...).call() 调用都会自动：
 *   - before：从 ChatMemory 取出该 conversation 的历史，附加到本次请求
 *   - after ：把 user 消息 + AI 回复写回 ChatMemory
 * 调用方无需手动 add / get。
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/api/memory2")
public class MemoryAdvisorController {

    @Resource
    private ChatClient.Builder chatClientBuilder;

    @Resource
    private ChatMemory chatMemory;

    /**
     * 硬编码两轮交互，演示 Advisor 自动记忆功能。
     * 第1轮告诉 AI "我叫徐庶"，第2轮问 "我叫什么"，AI 应能正确回答。
     * GET http://localhost:18080/api/memory2/test
     */
    @GetMapping("/test")
    public String testMemoryAdvisor() {
        // 把 PromptChatMemoryAdvisor 注册为默认 advisor
        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();

        StringBuilder result = new StringBuilder();

        // 第1轮
        String r1 = chatClient.prompt()
                .user("我叫徐庶")
                .call()
                .content();
        result.append("【第1轮】用户: 我叫徐庶\n");
        result.append("【第1轮】AI  : ").append(r1).append("\n\n");

        // 第2轮：同一个 chatClient 实例，自动从记忆读取上文
        String r2 = chatClient.prompt()
                .user("我叫什么")
                .call()
                .content();
        result.append("【第2轮】用户: 我叫什么\n");
        result.append("【第2轮】AI  : ").append(r2);

        return result.toString();
    }

    /**
     * 单条消息交互（手动传入 conversationId 和 message），
     * 同一 conversationId 的多次调用会自动保持上下文。
     * GET http://localhost:18080/api/memory2/chat?conversationId=user1&message=你好
     */
    @GetMapping("/chat")
    public String chat(
            @RequestParam(defaultValue = "default") String conversationId,
            @RequestParam(defaultValue = "你好") String message) {

        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();

        return chatClient.prompt()
                // 给 advisor 指定本次对话的 conversationId
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(message)
                .call()
                .content();
    }
}