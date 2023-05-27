package com.glmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.glmall.common.to.es.SkuEsModel;
import com.glmall.common.utils.R;
import com.glmall.search.configuration.RequestOptionsHolder;
import com.glmall.search.constant.EsConstant;
import com.glmall.search.feign.ProductFeignService;
import com.glmall.search.service.MallSearchService;
import com.glmall.search.vo.AttrRespVo;
import com.glmall.search.vo.SearchParam;
import com.glmall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam param) {
        SearchResult searchResult = null;
        SearchRequest searchReq = this.buildSearchRequest(param);

        try {
            // 执行查询
            SearchResponse searchResponse = restHighLevelClient.search(searchReq,
                    RequestOptionsHolder.getCommonOptions());
            searchResult = this.buildSearchResult(searchResponse, param);

        } catch (IOException e) {
            log.error("查询es报错", e);
        }

        return searchResult;
    }

    private SearchResult buildSearchResult(SearchResponse searchResponse, SearchParam param) {
        SearchHits hits = searchResponse.getHits();
        SearchResult result = new SearchResult();
        // note 1、返回所有查询到的商品
        SearchHit[] searchHits = hits.getHits();
        List<SkuEsModel> skuEsModels = null;
        if (searchHits != null && searchHits.length > 0) {
            List<SearchHit> searchHitList = Arrays.asList(searchHits);
            skuEsModels = searchHitList.stream().map(searchHit -> {
                String sourceStr = searchHit.getSourceAsString();
                SkuEsModel model = JSON.parseObject(sourceStr, SkuEsModel.class);
                // 如果带有关键字搜索，关键字需要进行高亮显示
                if (StringUtils.isNotBlank(param.getKeyword())) {
                    HighlightField skuTitle = searchHit.getHighlightFields().get("skuTitle");
                    String skuTitleStr = skuTitle.fragments()[0].string();
                    model.setSkuTitle(skuTitleStr);
                }
                return model;
            }).collect(Collectors.toList());
        }
        result.setProducts(skuEsModels);

        // note 2、当前商品所涉及到的所有属性信息
        ParsedNested attrAgg = searchResponse.getAggregations().get("attrAgg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        List<SearchResult.AttrVo> attrs =
                attrIdAgg.getBuckets().stream().map(bucket -> {
                    SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
                    long attrID = bucket.getKeyAsNumber().longValue();
                    attrVo.setAttrId(attrID);
                    // 属性名称子聚合
                    ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
                    List<? extends Terms.Bucket> buckets = attrNameAgg.getBuckets();
                    if (!CollectionUtils.isEmpty(buckets)) {
                        String attrName = buckets.stream().findAny().get().getKeyAsString();
                        attrVo.setAttrName(attrName);
                    }
                    // 属性值子聚合
                    ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
                    List<? extends Terms.Bucket> buckets1 = attrValueAgg.getBuckets();
                    List<String> attrValues = buckets1.stream().map(bucket1 -> bucket1.getKeyAsString())
                            .collect(Collectors.toList());
                    attrVo.setAttrValues(attrValues);
                    return attrVo;
                }).collect(Collectors.toList());
        result.setAttrs(attrs);

        // note 3、当前商品所涉及到的所有品牌信息
        ParsedLongTerms brandAgg = searchResponse.getAggregations().get("brandAgg");
        List<SearchResult.BrandVo> brands =
                brandAgg.getBuckets().stream().map(bucket -> {
                    SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
                    brandVo.setBrandId(bucket.getKeyAsNumber().longValue());
                    // 品牌名称子聚合
                    ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brandNameAgg");
                    List<? extends Terms.Bucket> buckets = brandNameAgg.getBuckets();
                    if (!CollectionUtils.isEmpty(buckets)) {
                        String brandName = buckets.stream().findAny().get().getKeyAsString();
                        brandVo.setBrandName(brandName);
                    }
                    // 品牌图片子聚合
                    ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brandImgAgg");
                    List<? extends Terms.Bucket> buckets1 = brandImgAgg.getBuckets();
                    if (!CollectionUtils.isEmpty(buckets1)) {
                        String brandImg = buckets1.stream().findAny().get().getKeyAsString();
                        brandVo.setBrandImg(brandImg);
                    }
                    return brandVo;
                }).collect(Collectors.toList());

        result.setBrands(brands);

        // note 4、当前商品所涉及到的所有分类信息
        ParsedLongTerms catalogAgg = searchResponse.getAggregations().get("catalogAgg");
        List<? extends Terms.Bucket> catalogAggBuckets = catalogAgg.getBuckets();
        List<SearchResult.CatalogVo> catalogVos = catalogAggBuckets.stream().map(bucket -> {
            Long catalogId = (Long) bucket.getKeyAsNumber();
            long docCount = bucket.getDocCount();
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            catalogVo.setCatalogId(catalogId);
            // 获取分类名子聚合
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalogNameAgg");
            if (catalogNameAgg != null) {
                String catalogName = catalogNameAgg.getBuckets().stream().
                        findFirst().get().getKeyAsString();
                catalogVo.setCatalogName(catalogName);
            }
            return catalogVo;
        }).collect(Collectors.toList());
        result.setCatalogs(catalogVos);

        // note 5、分页信息
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        Long totalPages = total % EsConstant.PRODUCT_PAGESIZE == 0
                ? (total / EsConstant.PRODUCT_PAGESIZE)
                : ((total / EsConstant.PRODUCT_PAGESIZE) + 1);
        result.setPageNo(param.getPageNo());
        result.setTotalPages(totalPages.intValue());

        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        // note 6. 构建面包屑导航功能
        if (!CollectionUtils.isEmpty(param.getAttrs())) {
            List<String> attrList = param.getAttrs();
            List<SearchResult.NavVo> navs =
                    attrList.stream().map(attr -> {
                        // 分析每一个attr传过来的查询参数
                        // attrs=1_其他:安卓&attrs=2_5寸:6寸
                        SearchResult.NavVo navVo = new SearchResult.NavVo();
                        String[] attrArr = attr.split("_");
                        String attrId = attrArr[0];
                        String attrValues = attrArr[1];
                        R info = productFeignService.info(Long.valueOf(attrId));
                        if (info.getCode() == 0) {
                            AttrRespVo attrRespVo = info.getDataByKey("attr", new TypeReference<AttrRespVo>() {
                            });
                            navVo.setNavName(attrRespVo.getAttrName());
                        } else {
                            navVo.setNavName(attrId);
                        }
                        navVo.setNavValue(attrValues);
                        // 取消面包屑以后，我们跳到哪个地方，将请求的url里的参数替换掉就可以了
                        // 拿到所有的查询条件，去掉当前条件
                        String queryString = param.get_queryString();
                        String encode = null;
                        try {
                            encode = URLEncoder.encode(attr, "UTF-8");
                            // 浏览器对;和空格的处理和java的差异
                            encode = encode.replace("%3B",";");
                            encode = encode.replace("+","%20");
                        } catch (UnsupportedEncodingException e) {
                            log.error("url编码错误：", e);
                        }
                        String replace = queryString.contains("?attrs")
                                ? queryString.replace("?attrs=" + encode, "")
                                : queryString.replace("&attrs=" + encode, "");
                        navVo.setLink("http://search.glmall.com/list.html?" + replace);
                        return navVo;
                    }).collect(Collectors.toList());
            result.setNavs(navs);
        }

        return result;
    }

    /**
     * 构建搜索请求
     *
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam searchParam) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        String keyword = searchParam.getKeyword();
        // note ===================整合查询Query条件
        BoolQueryBuilder boolQueryBuilder = this.buildBoolQueryBuilder(searchParam, keyword);
        // 查询条件
        sourceBuilder.query(boolQueryBuilder);

        // note =============整合查询排序、分页、高亮条件
        /*
         * sort=SaleCount_desc/asc
         * sort=skuPrice_desc/asc
         * sort=hotScore_desc/asc
         */
        this.buildSortAndPagableAndHightlightConditions(searchParam, sourceBuilder, keyword);

        // note =================== 整合聚合分析条件
        TermsAggregationBuilder brandAggBuilder = this.buildBrandTermsAggregationBuilder();
        sourceBuilder.aggregation(brandAggBuilder);

        // 分类聚合
        TermsAggregationBuilder catalogAggBuilder = this.buildCatalogTermsAggregationBuilder();
        sourceBuilder.aggregation(catalogAggBuilder);

        // 属性聚合-嵌套聚合
        NestedAggregationBuilder attrAggBuilder = this.buildNestedAggregationBuilder();
        sourceBuilder.aggregation(attrAggBuilder);

        String dsl = sourceBuilder.toString();
        log.info("dsl : {}", dsl);

        SearchRequest searchRequest = new SearchRequest(
                EsConstant.PRODUCT_INDEX);
        searchRequest.source(sourceBuilder);

        return searchRequest;
    }

    /**
     * 构建属性聚合条件
     *
     * @return
     */
    private NestedAggregationBuilder buildNestedAggregationBuilder() {
        NestedAggregationBuilder attrAggBuilder = AggregationBuilders
                .nested("attrAgg", "attrs");
        TermsAggregationBuilder attrIdAggBuilder = AggregationBuilders
                .terms("attrIdAgg")
                .field("attrs.attrId");
        TermsAggregationBuilder attrNameAggBuilder = AggregationBuilders
                .terms("attrNameAgg")
                .field("attrs.attrName");
        TermsAggregationBuilder attrValueAggBuilder = AggregationBuilders
                .terms("attrValueAgg")
                .field("attrs.attrValue").size(50);
        attrIdAggBuilder.subAggregation(attrNameAggBuilder);
        attrIdAggBuilder.subAggregation(attrValueAggBuilder);
        attrAggBuilder.subAggregation(attrIdAggBuilder);
        return attrAggBuilder;
    }

    /**
     * 构建分类聚合条件
     *
     * @return
     */
    private TermsAggregationBuilder buildCatalogTermsAggregationBuilder() {
        TermsAggregationBuilder catalogAggBuilder = AggregationBuilders
                .terms("catalogAgg")
                .field("catalogId");
        // 分类聚合-分类名子聚合
        catalogAggBuilder.subAggregation(AggregationBuilders
                .terms("catalogNameAgg")
                .field("catalogName"));
        return catalogAggBuilder;
    }

    /**
     * 构建品牌聚合条件
     *
     * @return
     */
    private TermsAggregationBuilder buildBrandTermsAggregationBuilder() {
        // 品牌聚合
        TermsAggregationBuilder brandAggBuilder = AggregationBuilders
                .terms("brandAgg")
                .field("brandId").size(50);
        // 品牌聚合-品牌名子聚合
        brandAggBuilder.subAggregation(AggregationBuilders
                .terms("brandNameAgg")
                .field("brandName").size(1));
        // 品牌聚合-品牌图片子聚合
        brandAggBuilder.subAggregation(AggregationBuilders
                .terms("brandImgAgg")
                .field("brandImg").size(1));
        return brandAggBuilder;
    }

    /**
     * 构建排序、分页、高亮条件
     *
     * @param searchParam
     * @param sourceBuilder
     * @param keyword
     */
    private void buildSortAndPagableAndHightlightConditions(SearchParam searchParam, SearchSourceBuilder sourceBuilder, String keyword) {
        String sort = searchParam.getSort();
        if (StringUtils.isNotBlank(sort) && sort.contains("_")) {
            String[] s = sort.split("_");
            SortBuilder sortBuilder = SortBuilders.fieldSort(s[0])
                    .order("desc".equals(s[1]) ? SortOrder.DESC : SortOrder.ASC);
            sourceBuilder.sort(sortBuilder);
        }
        // 高亮
        if (StringUtils.isNotBlank(keyword)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle")
                    .preTags("<b style='color:red'>").postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }
        // 分页
        // from = (pageNo-1)*size
        int fromNum = (searchParam.getPageNo() - 1) * EsConstant.PRODUCT_PAGESIZE;
        sourceBuilder.from(fromNum);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
    }

    /**
     * 构建查询条件
     *
     * @param searchParam
     * @param keyword
     * @return
     */
    private BoolQueryBuilder buildBoolQueryBuilder(SearchParam searchParam, String keyword) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(keyword)) {
            // must的全文检索
            MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("skuTitle", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }

        if (searchParam.getCatalog3Id() != null && searchParam.getCatalog3Id() != 0) {
            // filter的catalogId过滤条件
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("catalogId", searchParam.getCatalog3Id());
            boolQueryBuilder.filter(termQueryBuilder);
        }

        if (!CollectionUtils.isEmpty(searchParam.getBrandId())) {
            // filter的brandId过滤条件
            TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("brandId", searchParam.getBrandId());
            boolQueryBuilder.filter(termsQueryBuilder);
        }

        if (searchParam.getHasStock() != null) {
            // filter的 hasStock 过滤条件
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("hasStock", searchParam.getHasStock() == 1);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        String skuPrice = searchParam.getSkuPrice();
        // skuPrice=1_500/_500/500_
        if (StringUtils.isNotBlank(skuPrice) && skuPrice.contains("_")) {
            String[] s = skuPrice.split("_");
            RangeQueryBuilder rangeQueryBuilder = null;
            rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            if (s.length > 1) {
                rangeQueryBuilder.gte(StringUtils.isBlank(s[0]) ? 0 : s[0])
                        .lte(StringUtils.isBlank(s[1]) ? Integer.MAX_VALUE : s[1]);
            } else {
                if (skuPrice.endsWith("_")) {
                    rangeQueryBuilder.gte(StringUtils.isBlank(s[0]) ? 0 : s[0]);
                }
            }
            // filter的 skuPrice 的范围过滤条件
            if (null != rangeQueryBuilder) {
                boolQueryBuilder.filter(rangeQueryBuilder);
            }
        }

        List<String> attrs = searchParam.getAttrs();
        if (!CollectionUtils.isEmpty(attrs)) {
            // 每一个属性需要一个nestedQuery
            attrs.forEach(attr -> {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId))
                        .must(QueryBuilders.termsQuery("attrs.attrValue", Arrays.asList(attrValues)));
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs",
                        nestedBoolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            });
        }
        return boolQueryBuilder;
    }
}
