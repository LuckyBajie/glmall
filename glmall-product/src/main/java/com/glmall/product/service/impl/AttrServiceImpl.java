package com.glmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.glmall.common.constant.ProductConstant;
import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.Query;
import com.glmall.product.dao.AttrDao;
import com.glmall.product.entity.AttrAttrgroupRelationEntity;
import com.glmall.product.entity.AttrEntity;
import com.glmall.product.entity.AttrGroupEntity;
import com.glmall.product.entity.CategoryEntity;
import com.glmall.product.service.AttrAttrgroupRelationService;
import com.glmall.product.service.AttrGroupService;
import com.glmall.product.service.AttrService;
import com.glmall.product.service.CategoryService;
import com.glmall.product.vo.AttrResponseVo;
import com.glmall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Resource
    AttrGroupService attrGroupService;

    @Resource
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        // 保存基本属性
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attr, entity);
        this.save(entity);
        // 保存关联关系
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
            && attr.getAttrGroupId()!=null) {
            AttrAttrgroupRelationEntity atteGroupEntity = new AttrAttrgroupRelationEntity();
            atteGroupEntity.setAttrGroupId(attr.getAttrGroupId());
            atteGroupEntity.setAttrId(entity.getAttrId());
            attrAttrgroupRelationService.save(atteGroupEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(String attrType, Long catelogId, Map<String, Object> params) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_type",
                "base".equalsIgnoreCase(attrType) ?
                        ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                        : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode()
        );
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            // attr_id attr_name
            queryWrapper.eq("attr_id", key).or().like("attr_name", key);
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> list = page.getRecords();
        List<AttrResponseVo> voList = list.stream().map(attrEntity -> {
            AttrResponseVo vo = new AttrResponseVo();
            BeanUtils.copyProperties(attrEntity, vo);
            // 设置分类和分组的名字
            if ("base".equalsIgnoreCase(attrType)) {
                AttrAttrgroupRelationEntity aage = attrAttrgroupRelationService
                        .getOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq("attr_id", attrEntity.getAttrId()));
                if (aage != null && aage.getAttrGroupId()!=null) {
                    AttrGroupEntity attrGroup = attrGroupService.getById(aage.getAttrGroupId());
                    vo.setGroupName(attrGroup.getAttrGroupName());
                }
            }

            CategoryEntity category = categoryService.getById(attrEntity.getCatelogId());
            if (category != null) {
                vo.setCatelogName(category.getName());
            }

            return vo;
        }).collect(Collectors.toList());
        pageUtils.setList(voList);
        return pageUtils;
    }

    @Override
    public AttrResponseVo getAttrInfo(Long attrId) {
        AttrEntity attrEntity = this.getById(attrId);
        AttrResponseVo responseVo = new AttrResponseVo();
        BeanUtils.copyProperties(attrEntity, responseVo);

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            // 设置分组信息
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attrEntity.getAttrId()));
            if (relationEntity != null) {
                responseVo.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity attrGroup = attrGroupService.getById(relationEntity.getAttrGroupId());
                if (attrGroup != null) {
                    responseVo.setGroupName(attrGroup.getAttrGroupName());
                }
            }
        }

        // 设置分类信息
        Long catelogId = attrEntity.getCatelogId();
        List<Long> path = new ArrayList<>();
        attrGroupService.findCatogoryPath(catelogId, path);
        Collections.reverse(path);
        Long[] categoryPath = path.toArray(new Long[path.size()]);
        responseVo.setCatelogPath(categoryPath);

        CategoryEntity category = categoryService.getById(catelogId);
        if (category != null) {
            responseVo.setCatelogName(category.getName());
        }
        return responseVo;
    }

    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntiry = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntiry);
        this.updateById(attrEntiry);

        // 修改分组关联
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attr.getAttrId());
            long count = attrAttrgroupRelationService.count(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attr.getAttrId()));
            if (count > 0) {
                UpdateWrapper<AttrAttrgroupRelationEntity> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("attr_id", attr.getAttrId());
                attrAttrgroupRelationService.update(attrAttrgroupRelationEntity, updateWrapper);
            } else {
                attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
            }
        }
    }

    /**
     * 根据分组id查找所有的相关属性
     *
     * @param attrGroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> relationEntityList = attrAttrgroupRelationService
                .list(new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_group_id", attrGroupId));
        List<Long> attrIds = relationEntityList.stream().map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(attrIds)) {
            return null;
        }
        List<AttrEntity> attrEntities = this.listByIds(attrIds);
        return attrEntities;
    }

    /**
     * 获取当前分组没有关联的所有属性
     *
     * @param attrGroupId
     * @param params
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Long attrGroupId, Map<String, Object> params) {
        // 当前分组只能关联自己所属的分类里的所有属性
        AttrGroupEntity group = attrGroupService.getById(attrGroupId);
        Long catelogId = group.getCatelogId();
        // 当前分组只能关联别的分组没有引用的属性
        // 找到当前分类下的其他分组；
        List<AttrGroupEntity> groups = attrGroupService.list(
                new QueryWrapper<AttrGroupEntity>()
                        .eq("catelog_id", catelogId));
                        //.ne("attr_group_id", attrGroupId));
        List<Long> gids = groups.stream().map(AttrGroupEntity::getAttrGroupId)
                .collect(Collectors.toList());
        // 找到这些分组关联的属性
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationService.list(
                new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .in("attr_group_id", gids));
        List<Long> attrIds = relationEntities.stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
        // 从当前分类的所有属性中移除这些属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId).eq("attr_type",
                        ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (!CollectionUtils.isEmpty(attrIds)) {
            wrapper.notIn("attr_id", attrIds);
        }
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(w -> w.eq("attr_id", key)
                    .or().like("attr_name", key));
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

    @Override
    public List<Long> listSearchAttrIds(List<Long> attrIds) {
        List<AttrEntity> attrEntities = this.listByIds(attrIds);
        List<Long> ids = attrEntities.stream()
                .filter(attrEntity -> attrEntity.getSearchType() == 1)
                .map(AttrEntity::getAttrId)
                .collect(Collectors.toList());
        return ids;
    }


}