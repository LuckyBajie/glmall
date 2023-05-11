package com.glmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.glmall.common.utils.PageUtils;
import com.glmall.ware.entity.WareSkuEntity;

import java.util.Map;

/**
 * 商品库存
 *
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-24 22:36:12
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);
}

