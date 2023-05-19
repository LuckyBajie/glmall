package com.glmall.search;

import com.alibaba.fastjson.JSON;
import com.glmall.search.configuration.RequestOptionsHolder;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 将商品数据保存到es中：
 * 消费空间，节省时间
 * 1）方便检索
 * {
 *     skuId:1,
 *     spuId:11,
 *     skuTitle:华为,
 *     price:998,
 *     attrs:[
 *          {尺寸：6存},
 *          {cpu：高通},
 *          {分辨率：全高清}
 *     ]
 * }
 * 问题，存在数据冗余：
 * 100万*2K = 2000MB=2G
 *
 * 2）节省空间，浪费时间
 * sku索引
 *  {
 *     skuId:1,
 *     spuId:11,
 *     skuTitle:华为,
 *     price:998
 *  }
 *  attr索引
 *  {
 *      spuId:11,
 *      attrs:[
 *          {尺寸：6存},
 *          {cpu：高通},
 *          {分辨率：全高清}
 *      ]
 *  }
 *  问题：搜索小米：
 *      涵盖的分类：粮食、手机、电器
 *      带小米的商品有，10000个sku；涉及到4000个spu
 *      分步：
 *      查询4000个spu对应的所有可能属性：
 *      esClient：每次请求光传递spuId，
 *      数据量4000*8byte=32000Byte=32kb
 *      并发场景下：
 *      10000并发*32kb=320mb
 *      如果百万并发：
 *      1000000并发*32kb=32000mb=32GB
 *
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class GlmallSearchApplicationTests {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Data
    public class User {
        private String userName;
        private String gender;
        private Integer age;
    }

    /**
     * Copyright 2023 bejson.com
     */
    /**
     * Auto-generated: 2023-05-11 16:32:21
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    @Data
    public static class BankUser {
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;

    }

    /**
     * 测试检索数据
     */
    @Test
    void searchData() throws IOException {
        // basicSearch();

        // 创建检索请求
        SearchRequest searchRequest = new SearchRequest("bank");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 构建查询条件
        MatchQueryBuilder queryBuilder = QueryBuilders
                .matchQuery("address", "mill");
        sourceBuilder.query(queryBuilder);

        // 构建聚合条件
        AggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg")
                .field("age").size(10);
        AggregationBuilder ageAvg = AggregationBuilders.avg("ageAvg")
                .field("age");
        AggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg")
                .field("balance");
        sourceBuilder.aggregation(ageAgg)
                .aggregation(ageAvg)
                .aggregation(balanceAvg);

        // 分页
        sourceBuilder.size(10);
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptionsHolder.getCommonOptions());

        System.out.println(searchResponse);

        // 解析请求结果
        // 解析命中数据
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        List<SearchHit> searchHits = Arrays.asList(hits1);
        List<BankUser> bankUsers = searchHits.stream().map(hit -> {
            String sourceAsString = hit.getSourceAsString();
            BankUser bankUser = JSON.parseObject(sourceAsString, BankUser.class);
            return bankUser;
        }).collect(Collectors.toList());
        System.out.println("----------------------------------------------------------");
        System.out.println(JSON.toJSONString(bankUsers));

        System.out.println("----------------------------------------------------------");

        // 获取聚合信息
        Aggregations aggregations = searchResponse.getAggregations();
        System.out.println("ageAgg-----------------");
        Terms ageAgg1 = aggregations.get("ageAgg");
        ageAgg1.getBuckets().forEach(bucket -> {
            Long key = (Long) bucket.getKey();
            long docCount = bucket.getDocCount();
            System.out.println("key:"+key);
            System.out.println("docCount:"+docCount);
        });
        System.out.println("ageAvg-----------------");
        Avg ageAvg1 = aggregations.get("ageAvg");
        double value = ageAvg1.getValue();
        System.out.println("value:"+value);
        System.out.println("ageAvg-----------------");
        Avg balanceAvg1 = aggregations.get("balanceAvg");
        double value1 = balanceAvg1.getValue();
        System.out.println("value:"+value1);

        System.out.println("----------------------------------------------------------");
        List<Aggregation> aggregationList = searchResponse.getAggregations().asList();
        aggregationList.forEach(aggregation -> {
            String aggregationName = aggregation.getName();
            String type = aggregation.getType();
            Map<String, Object> metadata = aggregation.getMetadata();
            System.out.println("aggregationName->"+ aggregationName);
            System.out.println("type->"+ type);
            System.out.println("metadata->"+ JSON.toJSONString(metadata));
        });




    }

    private void basicSearch() throws IOException {
        // 创建检索请求
        SearchRequest searchRequest = new SearchRequest("bank");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 构建检索条件 - QueryBuilders
        MatchAllQueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        sourceBuilder.query(queryBuilder);
        // 构建排序规则 - SortBuilders
        FieldSortBuilder accountNumberSortBuilder = SortBuilders
                .fieldSort("account_number").order(SortOrder.ASC);
        FieldSortBuilder balanceSortBuilder = SortBuilders
                .fieldSort("balance").order(SortOrder.DESC);
        sourceBuilder.sort(accountNumberSortBuilder).sort(balanceSortBuilder);
        sourceBuilder.from(0).size(5);
        searchRequest.source(sourceBuilder);

        // 执行检索操作
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptionsHolder.getCommonOptions());

        // 分析结果
        System.out.println(searchResponse);
        // 获取命中记录
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
        }
    }

    /**
     * 测试存储数据
     */
    @Test
    void indexData() throws IOException {
        System.out.println(restHighLevelClient);
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        // 方式一，直接存kv对
        // indexRequest.source("userName","张三","age","18","gender","男");
        // 方式二，存json字符串
        User user = new User();
        user.setUserName("张三2");
        user.setAge(18);
        user.setGender("男");
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        // 其他方式...
        // 同步创建索引
        IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptionsHolder.getCommonOptions());
        System.out.println(response);


        // 异步创建索引，ConnectionClosedException: Connection closed unexpectedly
        /*restHighLevelClient.indexAsync(indexRequest,
                RequestOptionsHolder.getCommonOptions(), new ActionListener<IndexResponse>() {
                    @Override
                    public void onResponse(IndexResponse indexResponse) {
                        System.out.println("异步创建索引成功！"+ indexResponse.getResult().toString());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        System.out.println("异步创建索引失败！");
                        e.printStackTrace();

                    }
                });*/

    }

    @Test
    void contextLoads() {
        System.out.println(restHighLevelClient);
    }

}
