package com.glmall.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@SpringBootTest
class GlmallAuthApplicationTests {

    @Test
    void contextLoads() throws UnsupportedEncodingException {
        String s = "%7B%22RegionId%22%3A%22cn-hangzhou%22%2C%22InstanceId%22%3A%22api-shared-vpc-001%22%2C%22GroupId%22%3A%2248977d7b96074966a7c9c2a8872d7e06%22%2C%22ApiId%22%3A%222753c442639b43bcb8beb817b5a62727%22%2C%22AppKey%22%3A%22204201221%22%2C%22AppSecret%22%3A%22PkoKmLTb6G0jj5GJRCz7QpK0r8WWbaV8%22%2C%22AppCode%22%3A%224ab59e9bceb1409b81f762454a1b301a%22%2C%22AppMode%22%3A%22AppCode%22%2C%22SubDomain%22%3A%2248977d7b96074966a7c9c2a8872d7e06-cn-hangzhou.alicloudapi.com%22%2C%22CustomDomain%22%3A%22dfsns.market.alicloudapi.com%22%2C%22HttpMethod%22%3A%22POST%22%2C%22HttpProtocol%22%3A%22HTTP%22%2C%22Path%22%3A%22%2Fdata%2Fsend_sms%22%2C%22PathParams%22%3A%22%5B%5D%22%2C%22Headers%22%3A%22%5B%7B%5C%22ParameterName%5C%22%3A%5C%22Content-Type%5C%22%2C%5C%22ParameterValue%5C%22%3A%5C%22application%2Fx-www-form-urlencoded%3B%20charset%3Dutf-8%5C%22%2C%5C%22Required%5C%22%3A%5C%22OPTIONAL%5C%22%2C%5C%22isDisabled%5C%22%3Afalse%7D%5D%22%2C%22Query%22%3A%22%5B%5D%22%2C%22Body%22%3A%22%5B%7B%5C%22ParameterName%5C%22%3A%5C%22content%5C%22%2C%5C%22ParameterValue%5C%22%3A%5C%22code%3A1234%5C%22%2C%5C%22Required%5C%22%3A%5C%22REQUIRED%5C%22%7D%2C%7B%5C%22ParameterName%5C%22%3A%5C%22phone_number%5C%22%2C%5C%22ParameterValue%5C%22%3A%5C%2218934729793%5C%22%2C%5C%22Required%5C%22%3A%5C%22REQUIRED%5C%22%7D%2C%7B%5C%22ParameterName%5C%22%3A%5C%22template_id%5C%22%2C%5C%22ParameterValue%5C%22%3A%5C%22CST_ptdie100%5C%22%2C%5C%22Required%5C%22%3A%5C%22REQUIRED%5C%22%7D%5D%22%2C%22BodyFormat%22%3A%22FORM%22%2C%22StageName%22%3A%22RELEASE%22%2C%22Mock%22%3A%22FALSE%22%2C%22MockResult%22%3A%22%22%2C%22Purchased%22%3Atrue%2C%22DebugFrom%22%3A%22CloudMarket%22%7D";
        String decode = URLDecoder.decode(s, "UTF-8");
        System.out.println(decode);
    }

}
