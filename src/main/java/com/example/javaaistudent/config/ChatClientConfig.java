package com.example.javaaistudent.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * ChatClient 配置
 * 项目中同时存在 DashScopeChatModel 和 DeepSeekChatModel 两个 ChatModel bean，
 * ChatClientAutoConfiguration 无法决定用哪个，所以手动创建 ChatClient.Builder。
 * 这里指定使用 DashScope（通义千问）作为默认 ChatClient 的底层模型。
 * <p>
 * ChatMemory 使用 JDBC 仓库持久化到 MySQL（learn 库的 SPRING_AI_CHAT_MEMORY 表），
 * 保留最近 10 条消息。重启应用后对话历史不会丢失。
 *
 * @author Administrator
 */
@Configuration
public class ChatClientConfig {

    @Bean
    @Primary
    public ChatClient.Builder chatClientBuilder(DashScopeChatModel dashScopeChatModel) {
        return ChatClient.builder(dashScopeChatModel);
    }

    /**
     * JDBC 聊天记忆仓库，持久化到 MySQL
     * M1 版本无自动配置，需手动声明
     */
    @Bean
    public JdbcChatMemoryRepository chatMemoryRepository(JdbcTemplate jdbcTemplate) {
        return JdbcChatMemoryRepository.builder()
                .jdbcTemplate(jdbcTemplate)
                .build();
    }

    /**
     * 消息窗口记忆：注入 JDBC 仓库，重启服务记忆不丢失，保留最近 10 条消息
     */
    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory
                .builder()
                .maxMessages(10)
                .chatMemoryRepository(chatMemoryRepository)
                .build();
    }
}