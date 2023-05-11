package com.glmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.glmall.common.utils.PageUtils;
import com.glmall.product.entity.SpuInfoEntity;
import com.glmall.product.vo.spuvo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-24 16:23:12
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfo);

    PageUtils queryPageByCondition(Map<String, Object> params);
}

