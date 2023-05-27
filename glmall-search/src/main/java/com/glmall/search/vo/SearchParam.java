package com.glmall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面可能传递过来的查询条件
 * catalog3Id=225&keyword=小米&sort=SaleCount_desc&hasStock=0/1
 * &brandId=1&brandId=2
 *   * 好多的过滤条件：
 *   * hasStock（是否有货）、skuPrice区间、brandId、catalog3Id、attrs
 *   * hasStock=0/1
 *   * skuPrice=1_500/_500/500_
 *   * brandId=1&brandId=2
 *   * attrs=1_其他:安卓&attrs=2_5寸:6寸
 */
@Data
public class SearchParam {
    /**
     * 页面传递的搜索框文字
     * 全文检索：skuTitle
     */
    private String keyword;
    /**
     * 3级分类id，点击分类传递的参数
     */
    private Long catalog3Id;

    /**
     * 排序条件：
     * sort=SaleCount_desc/asc
     * sort=skuPrice_desc/asc
     * sort=hotScore_desc/asc
     */
    private String sort;

    /**
     * 是否有货
     * 0，无；1，有
     */
    private Integer hasStock;

    /**
     * 价格区间
     */
    private String skuPrice;

    /**
     * 品牌id
     */
    private List<Long> brandId;

    /**
     * 按照规格属性进行筛选
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNo = 1;

    /**
     * 原生的所有查询条件
     */
    private String _queryString;

}
