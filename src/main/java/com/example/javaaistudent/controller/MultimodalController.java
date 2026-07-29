package com.example.javaaistudent.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 多模态聊天（图文混排）Controller
 * qwen-omni-turbo 在 call() 模式下强制要求 enable_omni_output_audio_url=true
 * M1.1 builder 不暴露此参数，改用 stream() 模式绕过
 * GET http://localhost:18080/api/qwen/vl?prompt=描述图&file=E:/照片/zm/xushu.png
 */
@RestController
@RequestMapping("/api/qwen")
public class MultimodalController {

    private final DashScopeChatModel chatModel;

    public MultimodalController(DashScopeChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/vl")
    public String visionChat(
            @RequestParam(defaultValue = "识别图片") String prompt,
            @RequestParam String file) throws IOException {

        Path path = Paths.get(file);
        if (!path.isAbsolute()) {
            path = Paths.get("E:/照片/zm", file);
        }
        if (!Files.exists(path)) {
            return "文件不存在: " + path.toString();
        }

        Media media = new Media(
                MimeType.valueOf("image/jpeg"),
                new FileSystemResource(path));

        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .multiModel(true)
                .model("qwen-omni-turbo")
                .build();

        Prompt promptObj = Prompt.builder()
                .chatOptions(options)
                .messages(UserMessage.builder()
                        .text(prompt)
                        .media(media)
                        .build())
                .build();

        // stream() 模式不需要 enable_omni_output_audio_url，且只返回文字
        StringBuilder result = new StringBuilder();
        chatModel.stream(promptObj)
                .toIterable()
                .forEach(response -> result.append(
                        response.getResult().getOutput().getText()));

        return result.toString();
    }
}