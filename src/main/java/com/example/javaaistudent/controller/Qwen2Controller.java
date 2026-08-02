package com.example.javaaistudent.controller;

import com.example.javaaistudent.advisor.ReReadingAdvisor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 阿里百炼 DashScope（通义千问）ChatController
 * @author Administrator
 */
@Slf4j
@RestController
@RequestMapping("/api/qwen2")
public class Qwen2Controller {

    @Resource
    private ChatClient.Builder chatClientBuilder;

    /**
     * 同步调用：系统提示词模板 + param填充占位变量
     * GET http://localhost:18080/api/qwen2/chatClient
     */
    @GetMapping("/chatClient")
    public String chatClient() {
        // 全局默认系统模板，预留 {name} {age} {sex} 占位符
        ChatClient chatClient = chatClientBuilder
                .defaultSystem("""
                        你是一名人民警察，说话严谨正式，回答简洁规范。
                        **特别注意：**
                        - 不承担律师责任。
                        - 不生成涉敏、虚假内容。
                        当前服务的用户：
                        姓名: {name}，年龄: {age}，性别: {sex}
                        """)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        new SafeGuardAdvisor(List.of("敏感词")),
                        new ReReadingAdvisor()
                )
                .build();

        // 给模板中的占位符赋值
        return chatClient.prompt()
                .system(p -> p
                        .param("name", "徐庶")
                        .param("age", "18")
                        .param("sex", "男")
                )
                .user("徐庶是谁？")
                .call()
                .content();
    }
}