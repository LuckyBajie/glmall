package com.glmall.product.controller;

import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.R;
import com.glmall.product.entity.AttrEntity;
import com.glmall.product.entity.AttrGroupEntity;
import com.glmall.product.service.AttrAttrgroupRelationService;
import com.glmall.product.service.AttrGroupService;
import com.glmall.product.service.AttrService;
import com.glmall.product.vo.AttrGroupRelationVo;
import com.glmall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;


/**
 * 属性分组
 *
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-24 16:23:12
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Resource
    AttrService attrService;

    @Resource
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    // /product/attrgroup/225/withattr
    @RequestMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId) {
        // 查出当前分类下的所有属性分组
        // 查出每个属性分组的所有属性
        List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrGroupWithAttrs(catelogId);
        return R.ok().put("data", vos);
    }

    // /product/attrgroup/attr/relation
    @RequestMapping("/attr/relation")
    public R addAttrRelation(
            @RequestBody List<AttrGroupRelationVo> vos) {
        attrAttrgroupRelationService.saveBatch(vos);
        return R.ok();
    }

    // /product/attrgroup/{attrGroupId}/noattr/relation
    @RequestMapping("/{attrGroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrGroupId") Long attrGroupId,
                            @RequestParam Map<String, Object> params) {
        PageUtils page = attrService.getNoRelationAttr(attrGroupId, params);
        return R.ok().put("page", page);
    }

    // /product/attrgroup/attr/relation/delete
    @RequestMapping("/attr/relation/delete")
    // @RequiresPermissions("product:attrgroup:delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos) {
        attrGroupService.deleteRelation(vos);

        return R.ok();
    }

    // /product/attrgroup/{attrGroupId}/attr/relation
    @RequestMapping("/{attrGroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrGroupId") Long attrGroupId) {
        List<AttrEntity> entities = attrService.getRelationAttr(attrGroupId);

        return R.ok().put("data", entities);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catId}")
    // @RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("catId") Long catId) {
        // PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params, catId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    // @RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getInfoById(attrGroupId);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
