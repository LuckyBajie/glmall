package com.glmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.glmall.common.utils.PageUtils;
import com.glmall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-24 16:23:12
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetails(BrandEntity brand);
}

