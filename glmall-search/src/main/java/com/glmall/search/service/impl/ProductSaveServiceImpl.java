package com.glmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.glmall.common.to.es.SkuEsModel;
import com.glmall.search.configuration.RequestOptionsHolder;
import com.glmall.search.constant.EsConstant;
import com.glmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    @Resource
    RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        // 保存到es中
        // 1. 在es中建立索引:product，建立好映射关系
        // 已经在kibana中完成
        // 2.在es中保存这些数据
        BulkRequest bulkRequest = new BulkRequest();
        skuEsModels.forEach(skuEsModel -> {
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.index(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String s = JSON.toJSONString(skuEsModel);
            indexRequest.source(s, XContentType.JSON);

            bulkRequest.add(indexRequest);
        });
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest,
                RequestOptionsHolder.getCommonOptions());
        // todo 如果批量插入错误，处理错误
        //  有错误返回true
        boolean b = bulkResponse.hasFailures();
        if (b) {
            BulkItemResponse[] items = bulkResponse.getItems();
            List<BulkItemResponse> itemResponseList = Arrays.asList(items);
            log.error("商品上架错误：{}", bulkResponse);
        }

        return !b;
    }
}
