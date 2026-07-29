package com.example.javaaistudent;

import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechModel;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechOptions;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * 文字转语音
     * GET http://localhost:18080/api/audio/tts?text=你好&voice=longwan
     */
    @GetMapping("/tts")
    public String tts(
            @RequestParam(defaultValue = "大家好，我是人帅话好的徐庶") String text,
            @RequestParam(defaultValue = "longwan") String voice) throws IOException {

        // 配置：人声、模型、格式
        DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
                .voice(voice)             // 人声：longwan / longcheng / longhua ...
                .model("cosyvoice")
                .format("mp3")
                .build();

        // 调用合成
        TextToSpeechResponse response = speechModel.call(
                new TextToSpeechPrompt(text, options));

        byte[] audioBytes = response.getResult().getOutput();

        // 保存到本地
        Path saveDir = Paths.get(SAVE_DIR);
        Files.createDirectories(saveDir);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = timestamp + "_" + voice + ".mp3";
        Path savedPath = saveDir.resolve(filename);
        Files.write(savedPath, audioBytes);

        return "音频已保存: " + savedPath.toString();
    }
}