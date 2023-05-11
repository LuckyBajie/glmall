package com.glmall.ware.feign;

import com.glmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("glmall-gateway")
public interface ProductFeignService {
    /**
     * 信息
     * 1) 让所有请求过网关：
     *      a.@FeignClient("glmall-gateway")
     *      b./api/product/skuinfo/info/{skuId}
     * 2) 直接给对应的服务发请求：
     *      a. @FeignClient("glmall-product")
     *      b. /product/skuinfo/info/{skuId}
     */
    @RequestMapping("/api/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
