package com.example.javaaistudent.memory;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Redis 版聊天记忆，将对话历史持久化到 Redis。
 * 每个会话对应一个 Redis List，key 格式：chat:memory:{conversationId}
 *
 * @author Administrator
 */
public class RedisChatMemory implements ChatMemory {

    private static final String KEY_PREFIX = "chat:memory:";
    private static final int DEFAULT_MAX_MESSAGES = 10;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final int maxMessages;

    public RedisChatMemory(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this(redisTemplate, objectMapper, DEFAULT_MAX_MESSAGES);
    }

    public RedisChatMemory(StringRedisTemplate redisTemplate, ObjectMapper objectMapper, int maxMessages) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.maxMessages = maxMessages;
    }

    @Override
    public List<Message> get(String conversationId) {
        List<String> jsonList = redisTemplate.opsForList().range(buildKey(conversationId), 0, -1);
        if (jsonList == null || jsonList.isEmpty()) {
            return Collections.emptyList();
        }

        // 只取最后 maxMessages 条
        int size = jsonList.size();
        int start = Math.max(0, size - maxMessages);
        return jsonList.subList(start, size).stream()
                .map(this::deserialize)
                .collect(Collectors.toList());
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        String key = buildKey(conversationId);

        // 追加新消息
        List<String> jsonMessages = messages.stream()
                .map(this::serialize)
                .collect(Collectors.toList());
        redisTemplate.opsForList().rightPushAll(key, jsonMessages);

        // 只保留最近 maxMessages 条
        Long total = redisTemplate.opsForList().size(key);
        if (total != null && total > maxMessages) {
            redisTemplate.opsForList().trim(key, total - maxMessages, -1);
        }
    }

    /**
     * 清空指定会话的全部历史。
     */
    public void clear(String conversationId) {
        redisTemplate.delete(buildKey(conversationId));
    }

    private String buildKey(String conversationId) {
        return KEY_PREFIX + conversationId;
    }

    private String serialize(Message message) {
        try {
            Map<String, String> map = Map.of(
                    "type", message.getMessageType().name(),
                    "text", message.getText()
            );
            return objectMapper.writeValueAsString(map);
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to serialize message", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Message deserialize(String json) {
        try {
            Map<String, String> map = objectMapper.readValue(json, Map.class);
            String type = map.get("type");
            String text = map.get("text");

            return switch (type) {
                case "USER" -> new UserMessage(text);
                case "ASSISTANT" -> new AssistantMessage(text);
                case "SYSTEM" -> new SystemMessage(text);
                default -> new UserMessage(text);
            };
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to deserialize message", e);
        }
    }

    /**
     * Builder 模式，方便在 Config 类里构建。
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private StringRedisTemplate redisTemplate;
        private ObjectMapper objectMapper;
        private int maxMessages = DEFAULT_MAX_MESSAGES;

        public Builder redisTemplate(StringRedisTemplate redisTemplate) {
            this.redisTemplate = redisTemplate;
            return this;
        }

        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public Builder maxMessages(int maxMessages) {
            this.maxMessages = maxMessages;
            return this;
        }

        public RedisChatMemory build() {
            return new RedisChatMemory(redisTemplate, objectMapper, maxMessages);
        }
    }
}
