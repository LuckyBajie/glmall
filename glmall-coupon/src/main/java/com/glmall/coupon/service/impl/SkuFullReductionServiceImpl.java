package com.glmall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.glmall.common.to.MemberPrice;
import com.glmall.common.to.SkuReductionTo;
import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.Query;
import com.glmall.coupon.dao.SkuFullReductionDao;
import com.glmall.coupon.entity.MemberPriceEntity;
import com.glmall.coupon.entity.SkuFullReductionEntity;
import com.glmall.coupon.entity.SkuLadderEntity;
import com.glmall.coupon.service.MemberPriceService;
import com.glmall.coupon.service.SkuFullReductionService;
import com.glmall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Resource
    SkuLadderService skuLadderService;

    @Resource
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo reductionTo) {
        // 保存sku的优惠、满减等信息：
        //      glmall-sms -> sms_sku_ladder、、
        //      、
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(reductionTo.getSkuId());
        skuLadderEntity.setFullCount(reductionTo.getFullCount());
        skuLadderEntity.setDiscount(reductionTo.getDiscount());
        skuLadderEntity.setAddOther(reductionTo.getCountStatus());
        if (reductionTo.getFullCount() > 0) {
            skuLadderService.save(skuLadderEntity);
        }
        // sms_sku_full_reduction

        SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(reductionTo, reductionEntity);
        if (reductionEntity.getFullPrice().doubleValue() > 0) {
            this.save(reductionEntity);
        }

        //sms_member_price
        List<MemberPrice> memberPrices = reductionTo.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrices.stream().map(item -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(reductionTo.getSkuId());
            memberPriceEntity.setMemberLevelId(item.getId());
            memberPriceEntity.setMemberLevelName(item.getName());
            memberPriceEntity.setMemberPrice(item.getPrice());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).filter(item -> item.getMemberPrice().doubleValue() > 0).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);

    }


}