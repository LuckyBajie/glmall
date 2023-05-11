package com.glmall.product.dao;

import com.glmall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-24 16:23:12
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    void updateCatName(@Param("catId") Long catId, @Param("name") String name);
}
