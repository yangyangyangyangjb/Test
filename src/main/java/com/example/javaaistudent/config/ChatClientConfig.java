package com.example.javaaistudent.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * ChatClient 配置
 * 项目中同时存在 DashScopeChatModel 和 DeepSeekChatModel 两个 ChatModel bean，
 * ChatClientAutoConfiguration 无法决定用哪个，所以手动创建 ChatClient.Builder。
 * 这里指定使用 DashScope（通义千问）作为默认 ChatClient 的底层模型。
 */
@Configuration
public class ChatClientConfig {

    @Bean
    @Primary
    public ChatClient.Builder chatClientBuilder(DashScopeChatModel dashScopeChatModel) {
        return ChatClient.builder(dashScopeChatModel);
    }
}
