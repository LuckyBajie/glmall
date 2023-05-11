package com.glmall.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
