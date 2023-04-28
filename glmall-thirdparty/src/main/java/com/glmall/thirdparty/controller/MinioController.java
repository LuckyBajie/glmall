package com.glmall.thirdparty.controller;

import com.glmall.thirdparty.configuration.MinioPropertiesConfig;
import com.glmall.thirdparty.services.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Minio 接口
 *
 * @author Strive
 * @date 2022/4/25 10:48
 * @description
 */
@RestController
@RequestMapping("/thirdParty/minio")
@RequiredArgsConstructor
public class MinioController {

    private final MinioPropertiesConfig minioPropertiesConfig;

    private final MinioService minioService;

    @PostMapping("/upload")
    public String upload(@RequestParam(name = "file") MultipartFile multipartFile) {
        return minioService.upload(
                multipartFile, minioPropertiesConfig.getBucketName(), multipartFile.getContentType());
    }

    @GetMapping("/getObjectUrl")
    public String getObjectUrl(String objectName) {
        return minioService.getObjectUrl(minioPropertiesConfig.getBucketName(), objectName);
    }
}
