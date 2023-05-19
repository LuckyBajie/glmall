package com.glmall.search.controller;

import com.glmall.common.constant.BusinessErrorCodeEnum;
import com.glmall.common.to.es.SkuEsModel;
import com.glmall.common.utils.R;
import com.glmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/search/esSvae")
public class ElasticSaveController {

    @Resource
    private ProductSaveService productSaveService;


    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
        Boolean b = false;
        try {
            b = productSaveService.productStatusUp(skuEsModels);

        } catch (Exception e) {
            log.error("ElasticSaveController商品上架错误", e);
            return R.error(BusinessErrorCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),
                    BusinessErrorCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }

        if (b) {
            return R.ok();
        }

        return R.error(BusinessErrorCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),
                BusinessErrorCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
    }

}
