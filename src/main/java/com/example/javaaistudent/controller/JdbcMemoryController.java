package com.example.javaaistudent.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JDBC 持久化会话 Controller
 * 直接注入 {@link JdbcChatMemoryRepository}，通过底层 API 读写 MySQL 中的
 * SPRING_AI_CHAT_MEMORY 表，演示对话历史是如何被持久化的。
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/api/jdbc-memory")
public class JdbcMemoryController {

    private final JdbcChatMemoryRepository chatMemoryRepository;
    private final ChatClient chatClient;

    public JdbcMemoryController(ChatMemoryRepository chatMemoryRepository,
                                ChatClient.Builder chatClientBuilder) {
        // 配置类里已经把 JdbcChatMemoryRepository 声明为 Bean，Spring 会注入这个实现
        this.chatMemoryRepository = (JdbcChatMemoryRepository) chatMemoryRepository;

        // 用 JDBC 仓库构造 ChatMemory，并挂载 PromptChatMemoryAdvisor
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .chatMemoryRepository(this.chatMemoryRepository)
                .build();

        this.chatClient = chatClientBuilder
                .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * 列出当前数据库里保存的所有会话 ID。
     * GET http://localhost:18080/api/jdbc-memory/conversations
     */
    @GetMapping("/conversations")
    public List<String> listConversations() {
        return chatMemoryRepository.findConversationIds();
    }

    /**
     * 查询某个会话的历史消息（直接读数据库）。
     * GET http://localhost:18080/api/jdbc-memory/history?conversationId=user1
     */
    @GetMapping("/history")
    public List<String> getHistory(@RequestParam(defaultValue = "user1") String conversationId) {
        return chatMemoryRepository.findByConversationId(conversationId)
                .stream()
                .map(Message::getText)
                .collect(Collectors.toList());
    }

    /**
     * 直接往数据库里写一条用户消息。
     * GET http://localhost:18080/api/jdbc-memory/save?conversationId=user1&message=你好
     */
    @GetMapping("/save")
    public String saveMessage(@RequestParam(defaultValue = "user1") String conversationId,
                              @RequestParam(defaultValue = "你好") String message) {
        // 先读出现有消息，追加新消息后再整体 save（saveAll 会替换该 conversationId 下的全部记录）
        List<Message> messages = chatMemoryRepository.findByConversationId(conversationId);
        messages.add(new UserMessage(message));
        chatMemoryRepository.saveAll(conversationId, messages);
        return "已保存消息到 conversationId=" + conversationId;
    }

    /**
     * 使用 JDBC 版 ChatMemory 进行带上下文的对话。
     * 每次调用自动从 MySQL 读历史、追加新消息、把 AI 回复写回 MySQL。
     * GET http://localhost:18080/api/jdbc-memory/chat?conversationId=user1&message=我叫徐庶
     */
    @GetMapping("/chat")
    public String chat(@RequestParam(defaultValue = "user1") String conversationId,
                       @RequestParam(defaultValue = "你好") String message) {

        return chatClient.prompt()
                // 给 advisor 指定本次对话的 conversationId
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(message)
                .call()
                .content();
    }

    /**
     * 删除某个会话的全部历史。
     * GET http://localhost:18080/api/jdbc-memory/clear?conversationId=user1
     */
    @GetMapping("/clear")
    public String clear(@RequestParam(defaultValue = "user1") String conversationId) {
        chatMemoryRepository.deleteByConversationId(conversationId);
        return "已清空 conversationId=" + conversationId + " 的历史记录";
    }
}
