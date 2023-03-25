package com.glmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.glmall.common.utils.PageUtils;
import com.glmall.product.entity.AttrAttrgroupRelationEntity;

import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-24 16:23:12
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

