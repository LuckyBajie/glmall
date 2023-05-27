package com.glmall.search.feign;

import com.glmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("glmall-gateway")
public interface ProductFeignService {
    /**
     * 根据属性id获取属性信息
     */
    @RequestMapping("/api/product/attr/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId);
}
