package com.glmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.glmall.common.utils.PageUtils;
import com.glmall.product.entity.AttrEntity;
import com.glmall.product.vo.AttrResponseVo;
import com.glmall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-24 16:23:12
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(String attrType, Long catlogId, Map<String, Object> params);

    AttrResponseVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrGroupId);

    PageUtils getNoRelationAttr(Long attrGroupId, Map<String, Object> params);

    /**
     * 在所有属性id集合里，挑出所有检索属性
     * @param attrIds
     * @return
     */
    List<Long> listSearchAttrIds(List<Long> attrIds);
}

