package com.example.javaaistudent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
class TestDeepseek {

    @Autowired
    private DeepSeekChatModel chatModel;

    /**
     * 用 ChatModel + ChatOptions 调 DeepSeek（一次性返回完整结果）
     */
    @Test
    void testChatOptions() {
        // 1. 构造 ChatOptions（builder 模式，不可变）
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model("deepseek-v4-flash")   // ⚠️ DeepSeek V4 API 已废弃 deepseek-chat，必须用 v4 系列
                .temperature(0.1d)            // 温度：0=精确，2=发散
                .build();

        // 2. 用 Prompt 包装"用户输入 + 选项"
        Prompt prompt = new Prompt("请写一句诗描述清晨.", options);

        // 3. ChatModel.call 同步调用，返回 ChatResponse
        ChatResponse res = chatModel.call(prompt);

        // 4. ChatResponse → Result → Output(AssistantMessage) → getText() 拿到最终文本
        System.out.println(res.getResult().getOutput().getText());
    }

    /**
     * 流式调用：模型一边生成一边推送 token
     */
    @Test
    void testChatStream() {
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model("deepseek-v4-flash")
                .temperature(1.9d)
                .build();

        Prompt prompt = new Prompt("请写一首诗描述清晨", options);

        // stream() 返回 Flux<ChatResponse>，每个元素是一小段增量回复
        Flux<ChatResponse> stream = chatModel.stream(prompt);

        // toIterable() 把流聚合成可遍历的集合，逐段打印
        stream.toIterable().forEach(chatResponse -> {
            AssistantMessage message = chatResponse.getResult().getOutput();
            System.out.println(message.getText());
        });
    }
}