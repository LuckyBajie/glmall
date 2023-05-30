package com.glmall.thirdparty;

import com.alibaba.fastjson.JSON;
import com.glmall.common.utils.AliyunSmsHttpUtils;
import com.glmall.thirdparty.services.SmsService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.apache.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
class GlmallThirdpartyApplicationTests {

    @Test
    void contextLoads() {
        String res = "{\"status\":\"OK\",\"request_id\":\"TID4e6be9e9df794722958d9be8a9baf50c\"}";
        Map map = JSON.parseObject(res, HashMap.class);

        System.out.println("The end");
    }

    @Resource
    private MinioClient minioClient;

    @Resource
    private SmsService smsService;

    @Test
    public void sendSmsTest() throws Exception {
        smsService.sendSms("18934729793",
                "666888");
    }

    @Test
    public void sendSms() throws Exception{
        String host = "http://dfsns.market.alicloudapi.com";
        String path = "/data/send_sms";
        String method = "POST";
        String appcode = "4ab59e9bceb1409b81f762454a1b301a";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("content", "code:6666");
        bodys.put("phone_number", "18934729793");
        bodys.put("template_id", "CST_ptdie100");


        try {
            HttpResponse response = AliyunSmsHttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
