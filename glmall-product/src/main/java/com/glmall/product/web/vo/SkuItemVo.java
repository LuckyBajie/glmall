package com.glmall.product.web.vo;

import com.glmall.product.entity.SkuImagesEntity;
import com.glmall.product.entity.SkuInfoEntity;
import com.glmall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {
    // 1.sku 的基本信息获取,pms_sku_info
    private SkuInfoEntity skuInfo;

    private boolean hasStock = true;
    // 2.sku 的图片信息获取,pms_sku_images
    private List<SkuImagesEntity> images;
    // 3.spu 的销售属性组合信息获取,
    private List<ItemSaleAttrsVo> saleAttrsVos;
    // 4.spu 的介绍信息获取,
    private SpuInfoDescEntity description;
    // 5.spu 的规格参数信息获取,
    private List<SpuItemAttrGroupVo> groupAttrs;

}
