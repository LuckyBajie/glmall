package com.glmall.thirdparty.services;

import com.glmall.thirdparty.configuration.MinioPropertiesConfig;
import com.glmall.thirdparty.utils.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Minio 服务层
 *
 * @author Strive
 * @date 2022/4/25 10:32
 * @description Minio Service 层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioPropertiesConfig minioPropertiesConfig;

    private final MinioUtil minioUtil;

    /**
     * 检查存储桶是否存在
     *
     * @param bucketName 存储桶名称
     */
    public boolean bucketExists(String bucketName) {
        return minioUtil.bucketExists(bucketName);
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     */
    public void makeBucket(String bucketName) {
        minioUtil.makeBucket(bucketName);
    }

    /**
     * 文件上传
     *
     * @param file 上传的文件
     * @param bucketName 桶名称
     * @param fileType 文件类型
     */
    public String upload(MultipartFile file, String bucketName, String fileType) {
        if (StringUtils.isBlank(bucketName)) {
            log.info("bucketName is null");
        }

        try {
            if (!this.bucketExists(bucketName)) {
                this.makeBucket(bucketName);
            }
            String fileName = file.getOriginalFilename();

            assert fileName != null;
            String objectName =
                    UUID.randomUUID().toString().replace("-", "") + fileName.substring(fileName.lastIndexOf("."));
            String pathName = "/" + LocalDate.now().toString();
            minioUtil.putObject(bucketName, file, pathName, objectName, fileType);
            return minioPropertiesConfig.getEndpoint() + "/" + bucketName + pathName + "/" + objectName;
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败";
        }
    }

    /**
     * 获取文件路径
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     */
    public String getObjectUrl(String bucketName, String objectName) {
        return minioUtil.getObjectUrl(bucketName, objectName);
    }
}

