package com.glmall.search.vo;

import com.glmall.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {

    /**
     * 查询到的商品信息
     */
    private List<SkuEsModel> products;
    /**
     * 当前页码
     */
    private Integer pageNo;
    /**
     * 总记录数
     */
    private Long total;
    /**
     * 总页数
     */
    private Integer totalPages;
    /**
     * 可遍历的导航页
     */
    private List<Integer> pageNavs;

    /**
     * 查询结果所涉及的品牌
     */
    private List<BrandVo> brands;
    /**
     * 查询结果所涉及的分类
     */
    private List<CatalogVo> catalogs;
    /**
     * 查询结果所涉及到的所有属性
     */
    private List<AttrVo> attrs;

    // ============以上是返回给页面的所有信息============
    // 面包屑导航数据
    private List<NavVo> navs;


    /**
     * 面包屑导航vo
     */
    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;
    }

    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandImg;
        private String brandName;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValues;
    }

}
