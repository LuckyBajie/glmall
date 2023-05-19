package com.glmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.Query;
import com.glmall.product.dao.CategoryDao;
import com.glmall.product.entity.CategoryEntity;
import com.glmall.product.service.CategoryBrandRelationService;
import com.glmall.product.service.CategoryService;
import com.glmall.product.web.vo.Catalog2Vo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;

    /*
    @Resource
    CategoryDao categoryDao;
    */

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree(boolean level3NeedChildren) {
        // 1.查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        // 2.组装成父子的树形结构
        // 2.1) 找到所有的一级分类
        List<CategoryEntity> level1Menu = entities.stream()
                .filter(entitie -> entitie.getParentCid() == 0)
                .map(menu -> {
                    menu.setChildren(getChildren(menu, entities, level3NeedChildren));
                    return menu;
                }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort()))
                ).collect(Collectors.toList());
        // 2.2) 找到所有分类的子分类

        return level1Menu;
    }

    @Override
    public void removeMenusByIds(List<Long> ids) {
        // todo 1. Check the menus whether referenced by other place
        //  when they will be removed.

        // remove logically
        baseMapper.deleteBatchIds(ids);
    }

    @Transactional
    @Override
    public void updateDetail(CategoryEntity category) {
        this.updateById(category);
        // 同步更新其他关联表里的冗余数据
        if (!category.getName().isEmpty()) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());

            // todo 更新其他关联
        }
    }

    @Override
    public List<CategoryEntity> getLevel1Categories() {
        List<CategoryEntity> categoryEntities = this.list(new QueryWrapper<CategoryEntity>()
                .eq("parent_cid", 0));
        return categoryEntities;
    }

    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        /**
         * 1. 将数据库的多次查询变为一次
         */
        List<CategoryEntity> allCategories = this.list(null);
        // getCategoriesByParentId(allCategories,1l);
        // 查出所有1级分类分类

        List<CategoryEntity> level1Categories = this.getLevel1Categories();
        // 封装数据
        Map<String, List<Catalog2Vo>> map = level1Categories.stream().collect(Collectors.toMap(
                k -> k.getCatId().toString(),
                v -> {
                    // 每一个的一级分类，查到这个一级分类的二级分类
                    List<CategoryEntity> entities = getCategoriesByParentId(allCategories, v.getCatId());

                    // 封装上面的结果
                    List<Catalog2Vo> catalog2Vos = null;
                    if (!CollectionUtils.isEmpty(entities)) {
                        catalog2Vos = entities.stream().map(item -> {
                            Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(),
                                    null,
                                    item.getCatId().toString(),
                                    item.getName());
                            // 找当前二级分类的三级分类封装成vo
                            List<CategoryEntity> category3Entities = getCategoriesByParentId(allCategories, item.getCatId());
                            if (!CollectionUtils.isEmpty(category3Entities)) {
                                List<Catalog2Vo.Catalog3Vo> catalog3Vos = category3Entities.stream().map(l3Item -> {
                                    Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo
                                            .Catalog3Vo(item.getCatId().toString(),
                                            l3Item.getCatId().toString(),
                                            l3Item.getName());
                                    return catalog3Vo;
                                }).collect(Collectors.toList());
                                catalog2Vo.setCatalog3List(catalog3Vos);
                            }

                            return catalog2Vo;

                        }).collect(Collectors.toList());
                    }
                    return catalog2Vos;
                }


        ));
        return map;
    }

    private List<CategoryEntity> getCategoriesByParentId(List<CategoryEntity> allCategories, Long parentCid) {
        return allCategories.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == parentCid)
                .collect(Collectors.toList());
    }

    /**
     * 递归：为当前层级分类节点设置子分类
     *
     * @param root 当前上一级分类节点
     * @param all  所有分类数据
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root,
                                             List<CategoryEntity> all, boolean level3NeedChildren) {
        List<CategoryEntity> children = all.stream()
                // 过滤出当前层级的子分类
                .filter(categoryEntity -> categoryEntity.getParentCid() == root.getCatId())
                // 为子分类递归设置【子分类的子分类】
                .map(categoryEntity -> {
                    if (level3NeedChildren) {
                        categoryEntity.setChildren(getChildren(categoryEntity, all, true));
                    } else {
                        if (!categoryEntity.getCatLevel().equals(3)) {
                            categoryEntity.setChildren(getChildren(categoryEntity, all, false));
                        }
                    }
                    return categoryEntity;
                }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort()))
                ).collect(Collectors.toList());

        return children;
    }

}