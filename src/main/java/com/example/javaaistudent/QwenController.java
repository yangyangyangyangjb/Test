package com.example.javaaistudent;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 阿里百炼 DashScope（通义千问）ChatController
 * @author Administrator
 */
@RestController
@RequestMapping("/api/qwen")
public class QwenController {

    private final ChatClient chatClient;

    public QwenController(DashScopeChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    /**
     * 同步调用：一次性返回完整结果
     * GET http://localhost:18080/api/qwen/chat?message=你好
     */
    @GetMapping("/chat")
    public String chat(@RequestParam(defaultValue = "你好，请用一句话介绍你自己") String message) {
        return chatClient.prompt(message).call().content();
    }

    /**
     * 流式调用：SSE 方式逐 token 推送
     * GET http://localhost:18080/api/qwen/stream?message=写一首诗
     * 浏览器 EventSource 或 curl 均可消费
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
    public Flux<ServerSentEvent<String>> stream(@RequestParam(defaultValue = "写一首描写春天的五言绝句") String message) {
        return chatClient.prompt(message)
                .stream()
                .content()
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }
}
