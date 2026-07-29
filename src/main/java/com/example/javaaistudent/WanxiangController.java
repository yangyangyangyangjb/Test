package com.example.javaaistudent;

import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 通义万相（文生图）Controller
 * 生成的图片下载到 E:\照片\zm\
 * @author Administrator
 */
@RestController
@RequestMapping("/api/image")
public class WanxiangController {

    private static final String SAVE_DIR = "E:/照片/zm";
    private final ImageModel imageModel;

    public WanxiangController(DashScopeImageModel imageModel) {
        this.imageModel = imageModel;
    }

    /**
     * 文生图并下载到本地
     * GET http://localhost:18080/api/image/generate?prompt=一只猫
     */
    @GetMapping("/generate")
    public String generate(@RequestParam(defaultValue = "一只可爱的橘猫") String prompt) throws IOException {
        // 1. 调用通义万相生成图片
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model("wanx2.1-t2i-turbo")
                .build();

        ImageResponse response = imageModel.call(new ImagePrompt(prompt, options));
        String imageUrl = response.getResult().getOutput().getUrl();

        // 2. 确保保存目录存在
        Path saveDir = Paths.get(SAVE_DIR);
        Files.createDirectories(saveDir);

        // 3. 用原始 URL 下载（OSS 签名 URL 不能加额外 header，否则签名失效）
        byte[] imageBytes;
        try (InputStream is = URI.create(imageUrl).toURL().openStream()) {
            imageBytes = is.readAllBytes();
        }

        // 4. 写文件：时间戳_提示词前10字.png
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String safeName = prompt.length() > 10 ? prompt.substring(0, 10) : prompt;
        String filename = timestamp + "_" + safeName + ".png";
        Path savedPath = saveDir.resolve(filename);
        Files.write(savedPath, imageBytes);

        return "图片已保存: " + savedPath.toString();
    }
}
