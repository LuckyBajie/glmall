package com.glmall.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.R;
import com.glmall.product.entity.BrandEntity;
import com.glmall.product.entity.CategoryBrandRelationEntity;
import com.glmall.product.service.CategoryBrandRelationService;
import com.glmall.product.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// import org.apache.shiro.authz.annotation.RequiresPermissions;


/**
 * 品牌分类关联
 *
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-24 16:23:12
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;


    // /product/categorybrandrelation/brands/list
    /**
     * 获取分类下关联的品牌列表
     * 1. Controller 处理请求，接受和校验数据
     * 2. Service 接受controller传来的数据，进行业务处理
     * 3.Controller 接受Service处理完的数据，封装页面指定的数据
     * @return
     */
    @RequestMapping("/brands/list")
    public R relationBrandList(@RequestParam(value = "catId",required = true) Long catId) {

        List<BrandEntity> list = categoryBrandRelationService.getBrandsByCatId(catId);
        List<BrandVo> collect = list.stream().map(item -> {
            BrandVo vo = new BrandVo();
            vo.setBrandId(item.getBrandId());
            vo.setBrandName(item.getName());
            return vo;
        }).collect(Collectors.toList());
        return R.ok().put("data", collect);
    }
    /**
     * 获取品牌关联的分类列表
     * @param brandId
     * @return
     */
    @RequestMapping("/catelog/list")
    public R catlogList(@RequestParam("brandId") Long brandId) {
        List<CategoryBrandRelationEntity> list = categoryBrandRelationService
                .list(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
        return R.ok().put("data", list);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        // categoryBrandRelationService.save(categoryBrandRelation);
        categoryBrandRelationService.saveDetails(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
