package com.example.javaaistudent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
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
                .model("deepseek-v4-flash")// ⚠️ DeepSeek V4 API 已废弃 deepseek-chat，必须用 v4 系列
                .temperature(0.1d)        // 温度：0=精确，2=发散
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

    /**
     * 简化版流式调用：直接传字符串，跳过 Prompt/Options 包装
     * 应用配置的默认模型（application.yaml 里的 deepseek-v4-flash）
     */
    @Test
    void testDeepseekStream(@Autowired DeepSeekChatModel deepSeekChatModel) {
        // stream(String) 是 stream(Prompt) 的语法糖，省去构造 Prompt
        Flux<String> stream = deepSeekChatModel.stream("你好你是谁");
        stream.toIterable().forEach(System.out::println);
    }

    /**
     * 思维链（Reasoning Content）演示
     * 必须用推理类模型才能拿到 getReasoningContent() 的非空结果
     * 推荐使用：deepseek-reasoner 或开启了 thinking 的 deepseek-v4-pro
     */
    @Test
    void testDeepseek(@Autowired DeepSeekChatModel deepSeekChatModel) {
        // V4 API 已废弃 deepseek-reasoner，推理能力统一到 deepseek-v4-pro
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model("deepseek-v4-pro")
                .build();

        Prompt prompt = new Prompt("你好你是谁", options);
        ChatResponse response = deepSeekChatModel.call(prompt);

        // DeepSeekAssistantMessage 是 AssistantMessage 的子类，多了 reasoningContent 字段
        DeepSeekAssistantMessage assistantMessage =
                (DeepSeekAssistantMessage) response.getResult().getOutput();

        System.out.println("【思维链 / reasoning_content】");
        System.out.println(assistantMessage.getReasoningContent());
        System.out.println("-------------------------");
        System.out.println("【最终回复 / text】");
        System.out.println(assistantMessage.getText());
    }

    /**
     * 流式 + 思维链：先遍历一次拿 reasoning_content（AI 内心独白），
     * 再遍历一次拿 text（最终答案）。
     * 注意：Flux 每次订阅都会触发新的 API 请求，所以这是两次调用。
     * 用 deepseek-v4-pro 才能看到思维链内容。
     */
    @Test
    void testDeepseekStream2(@Autowired DeepSeekChatModel deepSeekChatModel) {
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model("deepseek-v4-pro")
                .build();

        Flux<ChatResponse> stream = deepSeekChatModel.stream(new Prompt("你好", options));

        // 第一轮：逐 chunk 打印思维链
        stream.toIterable().forEach(chatResponse -> {
            DeepSeekAssistantMessage assistantMessage =
                    (DeepSeekAssistantMessage) chatResponse.getResult().getOutput();
            System.out.println(assistantMessage.getReasoningContent());
        });

        System.out.println("-------------------------");

        // 第二轮：逐 chunk 打印最终文本
        stream.toIterable().forEach(chatResponse -> {
            DeepSeekAssistantMessage assistantMessage =
                    (DeepSeekAssistantMessage) chatResponse.getResult().getOutput();
            System.out.println(assistantMessage.getText());
        });
    }
}