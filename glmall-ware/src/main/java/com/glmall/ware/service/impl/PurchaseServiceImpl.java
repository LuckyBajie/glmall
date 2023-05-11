package com.glmall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.glmall.common.constant.WareConstant;
import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.Query;
import com.glmall.ware.dao.PurchaseDao;
import com.glmall.ware.entity.PurchaseDetailEntity;
import com.glmall.ware.entity.PurchaseEntity;
import com.glmall.ware.service.PurchaseDetailService;
import com.glmall.ware.service.PurchaseService;
import com.glmall.ware.service.WareSkuService;
import com.glmall.ware.vo.MergeVo;
import com.glmall.ware.vo.PurchaseFinishDetailVo;
import com.glmall.ware.vo.PurchaseFinishVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Resource
    PurchaseDetailService purchaseDetailService;

    @Resource
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryUnreceiveList(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0)
                        .or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo vo) {
        Long purchaseId = vo.getPurchaseId();
        if (purchaseId == null) {
            // 新建需求单
            PurchaseEntity entity = new PurchaseEntity();
            entity.setStatus(WareConstant.PurchaseStatusEnum.CREATED
                    .getStatusCode());
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            this.save(entity);
            purchaseId = entity.getId();
        }

        // todo 确认采购单状态是0或者1才能合并

        Long finalPurchaseId = purchaseId;
        List<Long> items = vo.getItems();

        List<PurchaseDetailEntity> collect = items.stream().map(i -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(i);
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getStatusCode());
            return detailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(collect);
        PurchaseEntity pe = new PurchaseEntity();
        pe.setId(purchaseId);
        pe.setUpdateTime(new Date());
        this.updateById(pe);


    }

    @Override
    public void receivePurchase(List<Long> ids) {
        //1、确认当前采购单是新建或者已分配状态
        List<PurchaseEntity> collect = ids.stream().map(id -> this.getById(id))
                .filter(ietm -> (ietm.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getStatusCode())
                        || (ietm.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getStatusCode()))
                .map(item -> {
                    item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getStatusCode());
                    item.setUpdateTime(new Date());
                    return item;
                }).collect(Collectors.toList());
        //2、改变采购单的状态
        this.updateBatchById(collect);
        //3、改变采购项的状态
        collect.forEach(item -> {
            Long id = item.getId();
            List<PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurchaseId(id);
            entities.forEach(entity -> {
                entity.setStatus(WareConstant.PurchaseDetailStatusEnum
                        .BUYING.getStatusCode());
            });
            purchaseDetailService.updateBatchById(entities);
        });

    }

    @Transactional
    @Override
    public void done(PurchaseFinishVo finishVo) {
        // 1、改变采购单的状态
        Long purchaseId = finishVo.getId();
        List<PurchaseFinishDetailVo> items = finishVo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        boolean flag = true;
        for (PurchaseFinishDetailVo item : items) {
            PurchaseDetailEntity purchaseDetail = new PurchaseDetailEntity();
            purchaseDetail.setStatus(item.getStatus());
            purchaseDetail.setId(item.getItemId());
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HAS_ERROR.getStatusCode()) {
                flag = false;
            } else {
                // 3、将成功采购的进行入库
                PurchaseDetailEntity byId = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(byId.getSkuId(),byId.getWareId(),byId.getSkuNum());
            }

            updates.add(purchaseDetail);
        }
        // 2、改变采购项的状态
        purchaseDetailService.updateBatchById(updates);

        PurchaseEntity pe = new PurchaseEntity();
        pe.setId(purchaseId);
        pe.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISH.getStatusCode()
                : WareConstant.PurchaseStatusEnum.HAS_ERROR.getStatusCode());
        pe.setUpdateTime(new Date());
        this.updateById(pe);
    }

}