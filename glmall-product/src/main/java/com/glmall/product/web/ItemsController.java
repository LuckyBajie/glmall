package com.glmall.product.web;

import com.glmall.product.service.SkuInfoService;
import com.glmall.product.web.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
public class ItemsController {

    @Resource
    SkuInfoService skuInfoService;

    @GetMapping({"item/{skuId}.html"})
    public String skuItems(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {

        SkuItemVo vo = skuInfoService.item(skuId);
        model.addAttribute("item", vo);
        return "item";
    }

}
