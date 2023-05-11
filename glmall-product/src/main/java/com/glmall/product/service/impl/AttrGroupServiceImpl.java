package com.glmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.Query;
import com.glmall.product.dao.AttrGroupDao;
import com.glmall.product.entity.AttrAttrgroupRelationEntity;
import com.glmall.product.entity.AttrEntity;
import com.glmall.product.entity.AttrGroupEntity;
import com.glmall.product.entity.CategoryEntity;
import com.glmall.product.service.AttrAttrgroupRelationService;
import com.glmall.product.service.AttrGroupService;
import com.glmall.product.service.AttrService;
import com.glmall.product.service.CategoryService;
import com.glmall.product.vo.AttrGroupRelationVo;
import com.glmall.product.vo.AttrGroupWithAttrsVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    CategoryService categoryService;

    @Resource
    AttrService attrService;

    @Resource
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();

        if (catId != null && catId != 0) {
            wrapper.eq("catelog_id", catId);
        }

        String key = (String) params.get("key");
        // where catelog_id = ? and (attr_group_id = ? or attr_group_name like ?)
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(w -> w.eq("attr_group_id", key).or().like("attr_group_name", key));
        }

        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    public AttrGroupEntity getInfoById(Long attrGroupId) {
        AttrGroupEntity attrGroup = getById(attrGroupId);
        List<Long> path = new ArrayList<>();
        findCatogoryPath(attrGroup.getCatelogId(), path);
        Collections.reverse(path);
        attrGroup.setCatePath(path.toArray(new Long[path.size()]));
        return attrGroup;
    }

    public void findCatogoryPath(Long catId, List<Long> path) {
        // 获取当前分组对应的分类信息
        CategoryEntity category = categoryService.getById(catId);
        path.add(catId);

        if (category.getParentCid() != 0) {
            findCatogoryPath(category.getParentCid(), path);
        }
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        // attrAttrgroupRelationService.remove(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id",).eq("attr_group_id"))

        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map(item -> {
            AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relation);
            return relation;
        }).collect(Collectors.toList());
        attrAttrgroupRelationService.deleteBatchRelation(entities);
    }

    /**
     * 根据分类id查出所有的分组信息，以及分组里的所有属性
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrs(Long catelogId) {
        // 查询分组信息
        List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));
        // 查询所有属性
        List<AttrGroupWithAttrsVo> collect = groupEntities.stream().map(item -> {
            AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(item, vo);
            List<AttrEntity> attrs = attrService.getRelationAttr(vo.getAttrGroupId());
            vo.setAttrs(CollectionUtils.isEmpty(attrs) ? new ArrayList<>(): attrs);
            return vo;
        }).collect(Collectors.toList());

        return collect;
    }

}