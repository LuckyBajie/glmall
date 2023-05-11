package com.glmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.glmall.common.to.SkuBoundTo;
import com.glmall.common.to.SkuReductionTo;
import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.Query;
import com.glmall.common.utils.R;
import com.glmall.product.dao.SpuInfoDao;
import com.glmall.product.entity.*;
import com.glmall.product.feign.CouponFeignService;
import com.glmall.product.service.*;
import com.glmall.product.vo.spuvo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {


    @Resource
    SpuInfoDescService spuInfoDescService;

    @Resource
    SpuImagesService spuImagesService;

    @Resource
    ProductAttrValueService productAttrValueService;

    @Resource
    AttrService attrService;

    @Resource
    SkuInfoService skuInfoService;

    @Resource
    SkuImagesService skuImagesService;

    @Resource
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Resource
    CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * todo 将来需要完善分布式事务的问题
     *
     * @param vo
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        // 1.保存spu基本信息，pms_spu_info
        SpuInfoEntity spuInfo = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfo);
        spuInfo.setCreateTime(new Date());
        spuInfo.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfo);

        // 2.保存spu的描述图片，pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDesc = new SpuInfoDescEntity();
        spuInfoDesc.setSpuId(spuInfo.getId());
        spuInfoDesc.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDesc);

        // 3.保存spu的图片集, pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfo.getId(), images);


        // 4.保存spu的规格参数，pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(item -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(item.getAttrId());
            AttrEntity attr = attrService.getById(item.getAttrId());
            valueEntity.setAttrName(attr.getAttrName());
            valueEntity.setAttrValue(item.getAttrValues());
            valueEntity.setQuickShow(item.getShowDesc());
            valueEntity.setSpuId(spuInfo.getId());
            return valueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);

        // 4.5 保存spu的积分信息：glmall-sms：sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SkuBoundTo to = new SkuBoundTo();
        BeanUtils.copyProperties(bounds, to);
        to.setSpuId(spuInfo.getId());
        R r = couponFeignService.saveSpuBounds(to);
        if (r.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }


        // 5.保存spu对应的所有sku信息:
        // 5.1 保存sku的基本信息，pms_sku_info
        List<Skus> skus = vo.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            skus.forEach(item -> {
                String defaultImg = "";
                Optional<Images> any = item.getImages().stream().filter(img -> img.getDefaultImg() == 1).findAny();
                if (any.isPresent()) {
                    defaultImg = any.get().getImgUrl();
                }
                SkuInfoEntity e = new SkuInfoEntity();
                BeanUtils.copyProperties(item, e);
                e.setBrandId(spuInfo.getBrandId());
                e.setCatalogId(spuInfo.getCatalogId());
                e.setSaleCount(0L);
                e.setSpuId(spuInfo.getId());
                e.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(e);
                Long skuId = e.getSkuId();

                // 5.2 保存sku的图片信息，pms_sku_images
                List<Images> images1 = item.getImages();
                List<SkuImagesEntity> imagesEntities = images1.stream().map(img -> {
                            SkuImagesEntity imagesEntity = new SkuImagesEntity();
                            imagesEntity.setSkuId(skuId);
                            imagesEntity.setImgUrl(img.getImgUrl());
                            imagesEntity.setDefaultImg(img.getDefaultImg());
                            return imagesEntity;
                        }).filter(value -> StringUtils.isNotBlank(value.getImgUrl()))
                        .collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);
                // 5.3 保存sku的销售属性信息：pms_sku_sale_attr_value
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> attrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity value = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, value);
                    value.setSkuId(skuId);
                    return value;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(attrValueEntities);
                // 5.4 保存sku的优惠、满减等信息：
                //      glmall-sms -> sms_sku_ladder、sms_sku_full_reduction、
                //      sms_member_price、
                SkuReductionTo reductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, reductionTo);
                reductionTo.setSkuId(skuId);
                if (reductionTo.getFullCount() > 0 || reductionTo.getFullPrice().doubleValue() > 0) {
                    R r1 = couponFeignService.saveSkuReduction(reductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程保存sku优惠信息失败");
                    }
                }
            });
        }
        log.info("商品发布，保存成功...");
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfo) {
        this.save(spuInfo);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(w -> w.eq("id", key)
                    .or().like("spu_name", key));
        }

        String status = String.valueOf(params.get("status"));
        if (StringUtils.isNotBlank(status)) {
            wrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotBlank(brandId)
                && !"0".equalsIgnoreCase(brandId)
                && !"null".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String catalogId = (String) params.get("catalogId");
        if (StringUtils.isNotBlank(catalogId)
                && !"0".equalsIgnoreCase(catalogId)
                && !"null".equalsIgnoreCase(catalogId)) {
            wrapper.eq("catalog_id", catalogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}