package com.example.javaaistudent.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * Re-Reading Advisor（再读一遍）
 * 实现 BaseAdvisor，在请求前重写 user 提示词，让 LLM "再读一遍"问题，
 * 常用于提升模型对复杂问题的理解准确率。
 *
 * @author Administrator
 */
public class ReReadingAdvisor implements BaseAdvisor {

    private static final String DEFAULT_USER_TEXT = """
            {re2_input_query}
            Read the question again: {re2_input_query}
            """;

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        // 用户提示词
        String contents = chatClientRequest.prompt().getContents();

        String re2InputQuery = PromptTemplate.builder()
                .template(DEFAULT_USER_TEXT)
                .build()
                .render(Map.<String, Object>of("re2_input_query", contents));

        ChatClientRequest clientRequest = chatClientRequest.mutate()
                .prompt(Prompt.builder().content(re2InputQuery).build())
                .build();
        return clientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}