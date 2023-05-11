package com.glmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.Query;
import com.glmall.product.dao.BrandDao;
import com.glmall.product.entity.BrandEntity;
import com.glmall.product.service.BrandService;
import com.glmall.product.service.CategoryBrandRelationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Resource
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new QueryWrapper<BrandEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 需要保证冗余字段的一致性
     *
     * @param brand
     */
    @Transactional
    @Override
    public void updateDetails(BrandEntity brand) {
        this.updateById(brand);
        // 同步更新其他关联表里的冗余数据
        if (!brand.getName().isEmpty()) {
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());

            // todo 更新其他关联
        }
    }

}