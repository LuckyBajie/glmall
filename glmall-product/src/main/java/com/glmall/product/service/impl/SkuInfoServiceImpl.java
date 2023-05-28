package com.glmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.Query;
import com.glmall.product.dao.SkuInfoDao;
import com.glmall.product.entity.SkuImagesEntity;
import com.glmall.product.entity.SkuInfoEntity;
import com.glmall.product.entity.SpuInfoDescEntity;
import com.glmall.product.service.*;
import com.glmall.product.web.vo.ItemSaleAttrsVo;
import com.glmall.product.web.vo.SkuItemVo;
import com.glmall.product.web.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity e) {
        this.save(e);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(w -> w.eq("sku_id", key)
                    .or().like("sku_name", key));
        }

        String catalogId = (String) params.get("catelogId");
        if (StringUtils.isNotBlank(catalogId) && !"0".equalsIgnoreCase(catalogId)) {
            wrapper.eq("catalog_id", catalogId);
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotBlank(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String min = params.get("min") == null ? "" : String.valueOf(params.get("min"));
        if (StringUtils.isNotBlank(min)) {
            wrapper.ge("price", min);
        }

        String max = params.get("max") == null ? "" : String.valueOf(params.get("max"));
        if (StringUtils.isNotBlank(max) && new BigDecimal(max).doubleValue() > 0) {
            wrapper.le("price", max);
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return list;
    }

    @Resource
    private SkuImagesService skuImagesService;

    @Resource
    private SpuInfoDescService spuInfoDescService;

    @Resource
    private AttrGroupService attrGroupService;

    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
        // 1.sku 的基本信息获取,pms_sku_info
        SkuInfoEntity skuInfo = this.getById(skuId);
        Long spuId = skuInfo.getSpuId();
        skuItemVo.setSkuInfo(skuInfo);
        // 2.sku 的图片信息获取,pms_sku_images
        List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
        skuItemVo.setImages(images);
        // 3.spu 的销售属性组合信息获取
        List<ItemSaleAttrsVo> saleAttrsVos = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
        skuItemVo.setSaleAttrsVos(saleAttrsVos);

        // 4.spu 的介绍信息获取,
        SpuInfoDescEntity descEntity = spuInfoDescService.getById(spuId);
        skuItemVo.setDescription(descEntity);

        // 5.spu 的规格参数信息获取,
        Long catalogId = skuInfo.getCatalogId();
        List<SpuItemAttrGroupVo> groupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
        skuItemVo.setGroupAttrs(groupVos);

        return skuItemVo;
    }

}