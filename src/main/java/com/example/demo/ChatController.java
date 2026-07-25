package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * GET 方式：浏览器地址栏直接访问
     * 示例：http://localhost:18080/api/chat?message=你好
     */
    @GetMapping
    public String chat(@RequestParam(defaultValue = "你好，请用一句话介绍你自己") String message) {
        return chatClient.prompt(message).call().content();
    }

    /**
     * POST 方式：JSON 请求体
     * 示例：curl -X POST http://localhost:18080/api/chat -H "Content-Type: application/json" -d "{\"message\":\"你好\"}"
     */
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String reply = chatClient.prompt(request.message()).call().content();
        return new ChatResponse(reply);
    }

    public record ChatRequest(String message) {}

    public record ChatResponse(String reply) {}
}