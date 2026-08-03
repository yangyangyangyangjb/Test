package com.example.javaaistudent.controller;

import com.example.javaaistudent.memory.RedisChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tools.jackson.databind.ObjectMapper;

/**
 * Redis 持久化会话 Controller
 * 使用 Redis 存储多轮对话历史，相比 JDBC 版读写更快。
 * 每个 conversationId 对应 Redis 中一个 List（key: chat:memory:{conversationId}），
 * 调用 get() 时只取最后 maxMessages 条，调用 add() 时 RPUSH + LTRIM 保持窗口大小。
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/api/redis-memory")
public class RedisMemoryController {

    private final RedisChatMemory chatMemory;
    private final ChatClient chatClient;

    public RedisMemoryController(StringRedisTemplate stringRedisTemplate,
                                 ObjectMapper objectMapper,
                                 ChatClient.Builder chatClientBuilder) {

        this.chatMemory = RedisChatMemory.builder()
                .redisTemplate(stringRedisTemplate)
                .objectMapper(objectMapper)
                .maxMessages(10)
                .build();

        this.chatClient = chatClientBuilder
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(this.chatMemory).build())
                .build();
    }

    /**
     * 使用 Redis 版 ChatMemory 进行带上下文的对话。
     * 每次调用自动从 Redis 读历史、追加新消息、把 AI 回复写回 Redis。
     *
     * GET http://localhost:18080/api/redis-memory/chat?conversationId=user1&message=我叫徐庶
     */
    @GetMapping("/chat")
    public String chat(@RequestParam(defaultValue = "user1") String conversationId,
                       @RequestParam(defaultValue = "你好") String message) {

        return chatClient.prompt()
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(message)
                .call()
                .content();
    }

    /**
     * 清空某个会话的全部历史（从 Redis 中删除 key）。
     * GET http://localhost:18080/api/redis-memory/clear?conversationId=user1
     */
    @GetMapping("/clear")
    public String clear(@RequestParam(defaultValue = "user1") String conversationId) {
        chatMemory.clear(conversationId);
        return "已从 Redis 清空 conversationId=" + conversationId + " 的历史记录";
    }
}
