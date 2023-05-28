package com.glmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.Query;
import com.glmall.product.dao.SkuSaleAttrValueDao;
import com.glmall.product.entity.SkuSaleAttrValueEntity;
import com.glmall.product.service.SkuSaleAttrValueService;
import com.glmall.product.web.vo.ItemSaleAttrsVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<ItemSaleAttrsVo> getSaleAttrsBySpuId(Long spuId) {
        /*
                SELECT
                ssav.`attr_id`,
                ssav.`attr_name`,
                ssav.`attr_value`,
                group_concat(distinct info.sku_id)
        FROM `pms_sku_info` info
        LEFT JOIN `pms_sku_sale_attr_value` ssav ON ssav.`sku_id`=info.`sku_id`
        WHERE info.`spu_id`=6
        GROUP BY ssav.`attr_id`,ssav.`attr_name`, ssav.`attr_value`
        */
        List<ItemSaleAttrsVo> vos = this.baseMapper.getSaleAttrsBySpuId(spuId);
        return vos;
    }

}