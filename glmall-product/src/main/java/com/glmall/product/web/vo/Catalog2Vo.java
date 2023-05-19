package com.glmall.product.web.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 二级分类vo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Catalog2Vo {
    /**
     * 一级父分类id
     */
    private String catalog1Id;
    /**
     * 三级子分类
     */
    private List<Catalog3Vo> catalog3List;
    /**
     * 当前分类id
     */
    private String id;
    /**
     * 分类名称
     */
    private String name;

    /**
     * 3级分类vo
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Catalog3Vo {
        /**
         * 二级父分类id
         */
        private String catalog2Id;
        private String id;
        private String name;

    }

}
