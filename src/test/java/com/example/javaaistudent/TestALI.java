package com.example.javaaistudent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.dashscope.DashScopeChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 阿里百炼 DashScope（通义千问）测试
 * 对应截图里的 TestALI.java
 * <p>
 * DashScope 不在 vanilla Spring AI 2.0 中，需要引入：
 * <pre>
 *   com.alibaba.cloud.ai:spring-ai-alibaba-bom:2.0.0-M1.1
 *   com.alibaba.cloud.ai:spring-ai-alibaba-starter-dashscope
 * </pre>
 */
@SpringBootTest
class TestALI {

    @Test
    void testQwen(@Autowired DashScopeChatModel dashScopeChatModel) {
        // call(String) 是 ChatModel 上的便捷方法，直接传用户消息，返回最终文本
        String content = dashScopeChatModel.call("你好你是谁");
        System.out.println(content);
    }
}