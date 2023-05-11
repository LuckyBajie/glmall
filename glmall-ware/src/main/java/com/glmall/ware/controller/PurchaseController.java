package com.glmall.ware.controller;

import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.R;
import com.glmall.ware.entity.PurchaseEntity;
import com.glmall.ware.service.PurchaseService;
import com.glmall.ware.vo.MergeVo;
import com.glmall.ware.vo.PurchaseFinishVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;



/**
 * 采购单信息
 *
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-24 22:36:12
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    // /ware/purchase/done
    @PostMapping("/done")
    public R finish(@RequestBody PurchaseFinishVo finishVo){
        purchaseService.done(finishVo);
        return R.ok();
    }

    /**
     * 领取采购单
     * @param ids
     * @return
     */
    // /ware/purchase/received [1,2,3]
    @PostMapping("/received")
    public R receivePurchase(@RequestBody List<Long> ids){
        purchaseService.receivePurchase(ids);
        return R.ok();
    }

    // /ware/purchase/merge
    /**
     * 采购需求合并整单
     *
     * {"purchaseId":1,"items":[1,2]}
     */
    @RequestMapping("/merge")
    // @RequiresPermissions("ware:purchase:list")
    public R merge(@RequestBody MergeVo vo){
        purchaseService.mergePurchase(vo);

        return R.ok();
    }

    // /ware/purchase/unreceive/list

    /**
     * 未被领取的采购单列表
     */
    @RequestMapping("/unreceive/list")
    // @RequiresPermissions("ware:purchase:list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryUnreceiveList(params);

        return R.ok().put("page", page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
