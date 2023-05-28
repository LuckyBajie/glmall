package com.glmall.product.web;

import com.glmall.product.service.CategoryService;
import com.glmall.product.service.SkuInfoService;
import com.glmall.product.web.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;

@Slf4j
@Controller
public class ItemsController {

    @Autowired
    CategoryService categoryService;

    @Resource
    SkuInfoService skuInfoService;

    @GetMapping({"item/{skuId}.html"})
    public String skuItems(@PathVariable("skuId") Long skuId, Model model) {

        SkuItemVo vo = skuInfoService.item(skuId);
        model.addAttribute("item", vo);
        return "item";
    }

}
