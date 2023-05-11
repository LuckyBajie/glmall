package com.glmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.Query;
import com.glmall.product.dao.SpuImagesDao;
import com.glmall.product.entity.SpuImagesEntity;
import com.glmall.product.service.SpuImagesService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveImages(Long spuInfoId, List<String> images) {
        if (CollectionUtils.isEmpty(images)) {
            return;
        }

        List<SpuImagesEntity> collect = images.stream().map(item -> {
            SpuImagesEntity entity = new SpuImagesEntity();
            entity.setSpuId(spuInfoId);
            entity.setImgUrl(item);
            return entity;
        }).collect(Collectors.toList());

        this.saveBatch(collect);
    }

}