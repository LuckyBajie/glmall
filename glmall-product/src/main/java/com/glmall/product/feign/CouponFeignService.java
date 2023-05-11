package com.glmall.product.feign;

import com.glmall.common.to.SkuBoundTo;
import com.glmall.common.to.SkuReductionTo;
import com.glmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("glmall-coupon")
public interface CouponFeignService {

    /**
     * 1.CouponFeignService.saveSpuBounds
     * 1.1 将参数对象转为json，传递到远端服务
     * 1.2找到远程服务的/coupon/spubounds/save方法，会将上一步转的json
     * 放在请求体中的位置，发送请求
     * 1.3对方服务收到请求，收到的是请求体里的json数据，
     * 将请求体的json转为SpuBoundsEntity的对象
     *
     * 【只要json数据模型是兼容的】，双方服务无需使用同一个to
     * SpuBoundsEntity spuBounds
     * @param to
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SkuBoundTo to);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo reductionTo);
}
