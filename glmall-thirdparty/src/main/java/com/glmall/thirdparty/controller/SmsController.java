package com.glmall.thirdparty.controller;

import com.alibaba.fastjson.JSON;
import com.glmall.common.constant.BusinessErrorCodeEnum;
import com.glmall.common.utils.R;
import com.glmall.thirdparty.services.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Sms 接口
 *
 * @author Strive
 * @date 2022/4/25 10:48
 * @description
 */
@RestController
@RequestMapping("/thirdParty/sms")
@RequiredArgsConstructor
public class SmsController {

    @Resource
    private SmsService smsService;

    @PostMapping("/send")
    public R sendSms(@RequestParam("phoneNum") String phoneNum, @RequestParam("code") String code) {
        String res = smsService.sendSms(phoneNum, code);
        Map map = JSON.parseObject(res, HashMap.class);
        String status = (String) map.get("status");

        if ("OK".equals(status)){
            return R.ok();
        }

        return R.error(BusinessErrorCodeEnum.REG_SMS_EXCEPTION);
    }
}
