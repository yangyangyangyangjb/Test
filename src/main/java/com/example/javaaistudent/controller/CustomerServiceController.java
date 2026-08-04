package com.example.javaaistudent.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 智能客服 Controller（基于 ChatClient 结构化输出做意图分流）。
 * <p>
 * 思路：
 * <ol>
 *   <li>先用 {@code .entity(Boolean.class)} 判断用户是否在投诉</li>
 *   <li>true  → 转人工客服（返回提示，由业务层接管）</li>
 *   <li>false → 走客服机器人自动回复（同一个 ChatClient）</li>
 * </ol>
 *
 * GET http://localhost:18080/api/customer-service/chat?message=你们家的快递迟迟不到，我要退货！
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/api/customer-service")
public class CustomerServiceController {

    /** 投诉识别 Prompt（参考网课 testBoolOut 的写法） */
    private static final String COMPLAINT_DETECT_PROMPT = """
            请判断用户信息是否表达了投诉意图?
            只能用 true 或 false 回答，不要输出多余内容
            """;

    /** 自动客服回复 Prompt */
    private static final String SERVICE_BOT_PROMPT = """
            你是一名电商平台的智能客服助手，请礼貌、简洁地回答用户的问题。
            如果无法回答，请引导用户联系人工客服。
            """;

    private final ChatClient chatClient;

    public CustomerServiceController(ChatClient.Builder chatClientBuilder) {
        // 单一 ChatClient 即可：检测 + 回复都用它，底层都是 qwen-plus
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 客服分流入口
     */
    @GetMapping("/chat")
    public Map<String, Object> chat(@RequestParam(defaultValue = "你们家的快递迟迟不到，我要退货！") String message) {

        // 第一步：投诉意图检测（结构化输出 → Boolean）
        Boolean isComplain = chatClient.prompt()
                .system(COMPLAINT_DETECT_PROMPT)
                .user(message)
                .call()
                .entity(Boolean.class);

        // 第二步：分支处理
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("input", message);
        resp.put("isComplain", isComplain);

        if (Boolean.TRUE.equals(isComplain)) {
            // true → 转人工客服（这里只标记，实际工单系统由业务侧接管）
            resp.put("route", "human_agent");
            resp.put("message", "已识别为投诉，转接人工客服。");
        } else {
            // false → 自动流转客服机器人
            String botReply = chatClient.prompt()
                    .system(SERVICE_BOT_PROMPT)
                    .user(message)
                    .call()
                    .content();
            resp.put("route", "auto_bot");
            resp.put("reply", botReply);
        }
        return resp;
    }
}