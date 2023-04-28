package com.glmall.thirdparty;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
class GlmallThirdpartyApplicationTests {

    @Test
    void contextLoads() {
    }

    @Resource
    private MinioClient minioClient;

    @Test
    public void putObjectTest() throws Exception{
        FileInputStream inputStream = new FileInputStream("C:\\mysoftware\\java_project_learn\\鼓励商城资料\\谷粒商城\\课件和文档\\基础篇\\资料\\pics\\0d40c24b264aa511.jpg");
        minioClient.putObject(
                PutObjectArgs.builder().bucket("glmall-product").object("/230427/230427/0d40c24b264aa511.jpg").stream(
                                inputStream, -1, 1073741824)
                        .contentType("image/jpeg")
                        .build());
    }



}
