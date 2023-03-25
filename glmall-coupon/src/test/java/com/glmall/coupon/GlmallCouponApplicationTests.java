package com.glmall.coupon;

import com.glmall.coupon.entity.CouponEntity;
import com.glmall.coupon.service.CouponService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class GlmallCouponApplicationTests {

    @Resource
    CouponService couponService;

    @Test
    void contextLoads() {
        CouponEntity coupon = new CouponEntity();
        coupon.setCouponName("好运不断");
        couponService.save(coupon);

        couponService.list().forEach(item->{
            System.out.println(item);
        });
    }

}
