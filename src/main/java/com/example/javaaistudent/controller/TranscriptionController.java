package com.example.javaaistudent.controller;

import com.alibaba.cloud.ai.dashscope.audio.transcription.AudioTranscriptionModel;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 语音转写（音频→文字）Controller
 * 接收本地音频文件路径，调用通义 Paraformer 转写为文本
 * 默认从 E:\照片\zm\ 读取
 * @author Administrator
 */
@RestController
@RequestMapping("/api/audio")
public class TranscriptionController {

    private static final String DEFAULT_DIR = "E:/照片/zm";
    private final AudioTranscriptionModel transcriptionModel;

    public TranscriptionController(AudioTranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    /**
     * 音频转文字
     * GET http://localhost:18080/api/audio/transcribe?file=20260729_220850_longwan.mp3
     */
    @GetMapping("/transcribe")
    public String transcribe(@RequestParam String file) throws IOException {
        Path path = Paths.get(file);
        if (!path.isAbsolute()) {
            path = Paths.get(DEFAULT_DIR, file);
        }
        if (!Files.exists(path)) {
            return "文件不存在: " + path.toString();
        }

        // AudioTranscriptionModel.call(Resource, Options) 有 M1.1 类型不匹配 bug
        // 改用无参 call(Resource)，model/format 走 yaml 默认配置
        String text = transcriptionModel.call(new FileSystemResource(path));
        return "转写结果: " + text;
    }
}
