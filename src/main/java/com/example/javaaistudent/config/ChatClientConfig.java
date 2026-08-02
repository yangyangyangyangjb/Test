package com.example.javaaistudent.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * ChatClient 配置
 * 项目中同时存在 DashScopeChatModel 和 DeepSeekChatModel 两个 ChatModel bean，
 * ChatClientAutoConfiguration 无法决定用哪个，所以手动创建 ChatClient.Builder。
 * 这里指定使用 DashScope（通义千问）作为默认 ChatClient 的底层模型。
 * <p>
 * 同时手动定义 ChatMemory Bean，避免依赖 spring.ai.chat.memory.* 配置自动装配失败。
 */
@Configuration
public class ChatClientConfig {

    @Bean
    @Primary
    public ChatClient.Builder chatClientBuilder(DashScopeChatModel dashScopeChatModel) {
        return ChatClient.builder(dashScopeChatModel);
    }

    /**
     * 内存版消息窗口记忆，默认保留最近 20 条消息
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().build();
    }
}