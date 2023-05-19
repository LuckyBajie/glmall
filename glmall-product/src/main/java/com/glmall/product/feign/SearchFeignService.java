package com.glmall.product.feign;

import com.glmall.common.to.es.SkuEsModel;
import com.glmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("glmall-gateway")
public interface SearchFeignService {

    @PostMapping("api/search/esSvae/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
