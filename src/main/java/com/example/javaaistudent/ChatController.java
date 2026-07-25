package com.example.javaaistudent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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
     GET 方式：浏览器地址栏直接访问
     示例：http://localhost:18080/api/chat/normal?message=你好
     */
    @GetMapping("/normal")
    public String normalChat(@RequestParam(defaultValue = "你好，请用一句话介绍你自己") String message) {
        return chatClient.prompt(message).call().content();
    }

    /**
     GET 方式：浏览器地址栏直接访问
     示例：http://localhost:18080/api/chat/stream?message=写一段100字的Java介绍
     */
    @GetMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8"
    )
    public Flux<ServerSentEvent<String>> streamChat(@RequestParam(defaultValue = "详细介绍SpringAI") String message) {
        return chatClient.prompt(message)
                .stream()
                .content()
                .map(chunk -> ServerSentEvent.<String>builder()
                        // JSON 序列化时会正确处理 UTF-8，每个 chunk 是一段 JSON 字符串
                        // 浏览器 EventSource 收到后会按 UTF-8 解析，中文不再乱码
                        .data(chunk)
                        .build());
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