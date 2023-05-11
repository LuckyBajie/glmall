package com.glmall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * {"id":1,
 * "items":[
 * {"itemId":1,"status":3,"reason":""},
 * {"itemId":2,"status":4,"reason":"无货"}
 * ]
 * }
 */
@Data
public class PurchaseFinishVo {


    /**
     * 采购单id
     */
    private Long id;
    /**
     * 采购需求项列表
     */
    private List<PurchaseFinishDetailVo> items;
}
