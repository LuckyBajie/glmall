package com.glmall.product.web;

import com.glmall.product.entity.CategoryEntity;
import com.glmall.product.service.CategoryService;
import com.glmall.product.web.vo.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/","index.html"})
    public String indexPage(Model model) {
        // 查出并展示一级分类
        List<CategoryEntity> categories = categoryService.getLevel1Categories();
        model.addAttribute("categories", categories);

        // public static final String DEFAULT_PREFIX = "classpath:/templates/";
        // public static final String DEFAULT_SUFFIX = ".html";
        // 视图解析器会进行拼串：
        // classpath:/templates/index.html
        return "index";
    }

    @ResponseBody
    @GetMapping({"/index/catalog.json"})
    public Map<String,List<Catalog2Vo>> getCatalogJson() {

        Map<String,List<Catalog2Vo>> map = categoryService.getCatalogJson();

        return map;
    }
}
