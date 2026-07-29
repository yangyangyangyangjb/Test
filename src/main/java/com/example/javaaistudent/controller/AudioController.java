package com.example.javaaistudent.controller;

import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechModel;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechOptions;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 通义 CosyVoice 语音合成 Controller
 * 生成的音频保存到 E:\照片\zm\
 * @author Administrator
 */
@RestController
@RequestMapping("/api/audio")
public class AudioController {

    private static final String SAVE_DIR = "E:/照片/zm";
    private final DashScopeAudioSpeechModel speechModel;

    public AudioController(DashScopeAudioSpeechModel speechModel) {
        this.speechModel = speechModel;
    }

    /**
     * 文字转语音（流式合成，收集后存为 mp3）
     * GET http://localhost:18080/api/audio/tts?text=你好
     */
    @GetMapping("/tts")
    public String tts(
            @RequestParam(defaultValue = "大家好，我是人帅话好的徐庶") String text,
            @RequestParam(defaultValue = "longwan") String voice) throws IOException {

        DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
                .voice(voice)
                .model("cosyvoice-v1")
                .format("mp3")
                .build();

        // 流式合成，收集所有 chunk
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        speechModel.stream(new TextToSpeechPrompt(text, options))
                .toIterable()
                .forEach(response -> {
                    try {
                        buffer.write(response.getResult().getOutput());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        byte[] audioBytes = buffer.toByteArray();

        Path saveDir = Paths.get(SAVE_DIR);
        Files.createDirectories(saveDir);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = timestamp + "_" + voice + ".mp3";
        Path savedPath = saveDir.resolve(filename);
        Files.write(savedPath, audioBytes);

        return "音频已保存: " + savedPath.toString();
    }
}
