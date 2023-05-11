package com.glmall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVo {
    // {"purchaseId":1,"items":[1,2]}

    /**
     * 采购单id
     */
    private Long purchaseId;
    /**
     * 采购需求项
     */
    private List<Long> items;
}
