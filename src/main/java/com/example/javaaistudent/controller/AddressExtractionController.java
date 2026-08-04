package com.example.javaaistudent.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 地址信息抽取 Controller（基于 ChatClient 结构化输出 entity(record)）。
 * <p>
 * 用法：把一整段包含收货信息的文本丢给模型，自动提取成 {@link Address} 记录。
 *
 * GET http://localhost:18080/api/address/extract?text=收货人: 张三, 电话13588888888. 地址: 浙江省杭州市西湖区文一西路 100 号
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/api/address")
public class AddressExtractionController {

    private static final String EXTRACT_PROMPT = """
            请从下面这条文本中提取收货信息。
            按字段精确抽取：
              - name     收件人姓名
              - phone    联系电话
              - province 省
              - city     市
              - district 区/县
              - detail   详细地址（街道、门牌号等）
            找不到的字段填空字符串，不要编造。
            """;

    private final ChatClient chatClient;

    public AddressExtractionController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 从文本里抽取收货地址
     */
    @GetMapping("/extract")
    public Address extract(@RequestParam(defaultValue = "收货人: 张三, 电话13588888888. 地址: 浙江省杭州市西湖区文一西路 100 号") String text) {
        return chatClient.prompt()
                .system(EXTRACT_PROMPT)
                .user(text)
                .call()
                .entity(Address.class);
    }

    /**
     * 收货地址记录（与截图一致）
     *
     * @param name     收件人姓名
     * @param phone    联系电话
     * @param province 省
     * @param city     市
     * @param district 区/县
     * @param detail   详细地址
     */
    public record Address(
            String name,
            String phone,
            String province,
            String city,
            String district,
            String detail
    ) {}
}