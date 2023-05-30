package com.glmall.auth.feign;

import com.glmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("glmall-gateway")
public interface ThirdPartyFeignService {

    /**
     * 发送短信验证码
     * @param phoneNum
     * @param code
     * @return
     */
    @PostMapping("/api/thirdParty/sms/send")
    R sendSms(@RequestParam("phoneNum") String phoneNum, @RequestParam("code") String code);

}
