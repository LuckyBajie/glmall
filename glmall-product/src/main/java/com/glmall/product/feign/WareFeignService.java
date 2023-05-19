package com.glmall.product.feign;

import com.glmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("glmall-ware")
public interface WareFeignService {

    @PostMapping("ware/waresku/hasStock")
    public R getSkusStock(@RequestBody List<Long> skuIds);
}
