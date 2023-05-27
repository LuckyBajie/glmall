package com.glmall.search.web.controller;

import com.glmall.search.service.MallSearchService;
import com.glmall.search.vo.SearchParam;
import com.glmall.search.vo.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {

    @Resource
    MallSearchService mallSearchService;

    @GetMapping({"/list.html","/"})
    public String listPage(SearchParam param, Model model, HttpServletRequest request){
        String queryString = request.getQueryString();
        param.set_queryString(queryString);
        // 根据页面查询条件，去es中查询商品结果
        SearchResult result = mallSearchService.search(param);
        model.addAttribute("result", result);
        return "list";
    }
}
