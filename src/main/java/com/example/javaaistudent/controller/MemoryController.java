package com.example.javaaistudent.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 多轮对话记忆 Controller
 * 基于 ChatMemory + conversationId 实现多轮上下文记忆，
 * 同一会话下模型能记住之前的对话内容。
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/api/memory")
public class MemoryController {

    /**
     * 内存版消息窗口记忆（默认保留最近 20 条消息）
     */
    private final ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

    private final DashScopeChatModel chatModel;

    public MemoryController(DashScopeChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 硬编码两轮交互，演示记忆功能。
     * 第1轮告诉 AI "我叫徐庶"，第2轮问 "我叫什么"，AI 应能正确回答。
     * GET http://localhost:18080/api/memory/test
     */
    @GetMapping("/test")
    public String testMemory() {
        String conversationId = "xs001";
        StringBuilder result = new StringBuilder();

        // ---- 第 1 轮 ----
        UserMessage userMessage1 = new UserMessage("我叫徐庶");
        chatMemory.add(conversationId, userMessage1);
        ChatResponse response1 = chatModel.call(new Prompt(chatMemory.get(conversationId)));
        chatMemory.add(conversationId, response1.getResult().getOutput());

        result.append("【第1轮】用户: 我叫徐庶\n");
        result.append("【第1轮】AI  : ").append(response1.getResult().getOutput().getText()).append("\n\n");

        // ---- 第 2 轮：测试模型是否记得上文的 "徐庶" ----
        UserMessage userMessage2 = new UserMessage("我叫什么");
        chatMemory.add(conversationId, userMessage2);
        ChatResponse response2 = chatModel.call(new Prompt(chatMemory.get(conversationId)));
        chatMemory.add(conversationId, response2.getResult().getOutput());

        result.append("【第2轮】用户: 我叫什么\n");
        result.append("【第2轮】AI  : ").append(response2.getResult().getOutput().getText());

        return result.toString();
    }

    /**
     * 单条消息交互（手动传入 conversationId 和 message），
     * 同一 conversationId 的多次调用会保持上下文。
     * GET http://localhost:18080/api/memory/chat?conversationId=user1&message=你好
     */
    @GetMapping("/chat")
    public String chat(
            @RequestParam(defaultValue = "user1") String conversationId,
            @RequestParam(defaultValue = "你好") String message) {

        // 记录用户消息
        chatMemory.add(conversationId, new UserMessage(message));

        // 把整段历史交给模型
        ChatResponse response = chatModel.call(new Prompt(chatMemory.get(conversationId)));

        // 把模型回复写回记忆
        chatMemory.add(conversationId, response.getResult().getOutput());

        return response.getResult().getOutput().getText();
    }
}