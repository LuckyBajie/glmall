package com.glmall.thirdparty.services;

import com.glmall.common.utils.AliyunSmsHttpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@ConfigurationProperties(prefix = "sms.config")
@Slf4j
@Data
@Service
public class SmsService {
    private String host;
    private String path;
    private String method;
    private String appcode;
    private String templateId;

    public String sendSms(String phoneNumber, String code) {
//        String host = "http://dfsns.market.alicloudapi.com";
//        String path = "/data/send_sms";
//        String method = "POST";
//        String appcode = "4ab59e9bceb1409b81f762454a1b301a";
        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<>();
        Map<String, String> bodys = new HashMap<>();
        bodys.put("content", "code:" + code);
        bodys.put("phone_number", phoneNumber);
        bodys.put("template_id", templateId);
//        bodys.put("template_id", "CST_ptdie100");
        try {
            HttpResponse response = AliyunSmsHttpUtils.doPost(host, path, method, headers, querys, bodys);
            log.info("短信发送结果：{}", response.toString());
            //获取response的body
            String res = EntityUtils.toString(response.getEntity());
            log.info("body:{}", res);
            return res;
        } catch (Exception e) {
            log.info("短信发送异常：", e);
        }

        return null;
    }
}

