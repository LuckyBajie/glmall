package com.glmall.common.to;

import lombok.Data;

import java.io.Serializable;

@Data
public class SkuHasStockTo implements Serializable {

    private static final long serialVersionUID = 5186478255816429230L;

    private Long skuId;
    private Boolean hasStock;
}
