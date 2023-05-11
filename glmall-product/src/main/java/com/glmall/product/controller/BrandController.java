package com.glmall.product.controller;

import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.R;
import com.glmall.product.entity.BrandEntity;
import com.glmall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;


/**
 * 品牌
 *
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-24 16:23:12
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    // @RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:brand:save")
//    public R save(@Valid @RequestBody BrandEntity brand, BindingResult bindingResult) {
    public R save(@Valid @RequestBody BrandEntity brand) {

        /*Map<String, String> map = new HashMap<>();
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().stream()
                    .forEach(fieldError -> map.put(fieldError.getField(), fieldError.getDefaultMessage()));
                    // .collect(Collectors.toMap(fieldError -> fieldError.getField(), fieldError -> fieldError.getDefaultMessage()));
            return R.error(400, "请求参数错误").put("err", map);
        }*/

        brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:brand:update")
    public R update(@RequestBody BrandEntity brand) {
        // brandService.updateById(brand);
        brandService.updateDetails(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
