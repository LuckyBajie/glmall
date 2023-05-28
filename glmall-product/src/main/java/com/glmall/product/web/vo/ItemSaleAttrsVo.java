package com.glmall.product.web.vo;

import lombok.Data;

import java.util.List;

@Data
public class ItemSaleAttrsVo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;
}