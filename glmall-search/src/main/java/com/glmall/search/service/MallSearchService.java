package com.glmall.search.service;

import com.glmall.search.vo.SearchParam;
import com.glmall.search.vo.SearchResult;

public interface MallSearchService {
    /**
     *
     * @param param 检索参数
     * @return 检索结果
     */
    SearchResult search(SearchParam param);
}
