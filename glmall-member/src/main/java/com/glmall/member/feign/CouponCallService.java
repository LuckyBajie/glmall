package com.glmall.member.feign;

import com.glmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("glmall-coupon")
public interface CouponCallService {

    @RequestMapping("coupon/coupon/openFeignTest")
    R openFeignTest();

}
