package com.glmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.glmall.common.to.SkuReductionTo;
import com.glmall.common.utils.PageUtils;
import com.glmall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-25 17:51:00
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo reductionTo);
}

