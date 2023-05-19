package com.glmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
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
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

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

    /**
     * todo 产生堆外内存溢出：OutOfDirectMemoryError
     *  1) SpringBoot2.0以后，默认使用 lettuce 作为操作redis的客户端，
     *  它使用netty作为网络通信，由 lettuce 的bug导致这个错误
     *  netty如果没有指定堆外内存，默认使用-Xmx这个vm参数设置的内存作为
     *  堆外内存的大小限制
     *  解决办法：
     *      a.升级lettuce客户端
     *      b.更换使用别的客户端如jedis等，来替代lettuce与redis交互
     * <p>
     * note：不能仅仅是手动设置lettuce的堆外内存大小，因为随着运行时间
     *  的加长，lettuce终究会报这个错误。
     *  通过-Dio.netty.maxDirectMemory进行设置
     *
     * @return
     */
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        /**
         * todo
         *  1、空结果缓存，解决缓存穿透
         *  2、设置随机过期时间，解决缓存雪崩
         *  3、加锁，解决缓存击穿
         *    note: 使用类似synchronized的本地同步锁，在分布式场景下是不够完美的，
         *     但是可以使用；
         *     解决：使用分布式锁
         */
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        // 1.加入缓存逻辑
        String catalogJson = ops.get("catalogJson");
        // 缓存中没有，从数据库中获取
        if (StringUtils.isBlank(catalogJson)) {
            Map<String, List<Catalog2Vo>> catalogJsonFromDb = this.getCatalogJsonFromDbWithRedisLock();
            return catalogJsonFromDb;
        }

        // fastJson复杂类型的反序列化
        Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson,
                new TypeReference<Map<String, List<Catalog2Vo>>>() {
                });
        return result;
    }

    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        /*    note: 使用类似synchronized的本地同步锁，在分布式场景下是不够完美的，
         *     但是可以使用；
         *     解决：使用分布式锁可以锁住所有服务
         */
        try {
            String uuid = UUID.randomUUID().toString();
            Boolean lock = stringRedisTemplate.opsForValue()
                    .setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
            if (lock) {
                Map<String, List<Catalog2Vo>> categoriesData;
                // 加锁成功
                try {
                    categoriesData = this.getCategoriesData();
                }finally {
                    // 删除分布式锁，也必须是原子操作,得使用lua脚本来保证删锁的原子性
                    // 返回1成功，0失败
                    String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1] "
                            + "then return redis.call('del', KEYS[1]) else return 0 end";
                    Integer delResult = stringRedisTemplate.execute(
                            new DefaultRedisScript<Integer>(luaScript, Integer.class),
                            Arrays.asList("lock", uuid));
                    // 下面这种加锁存在问题
//                String lockValue = stringRedisTemplate.opsForValue().get("lock");
//                // 删除自己的锁
//                if (lockValue.equals(uuid)) {
//                    stringRedisTemplate.delete("lock");
//                }
                }


                return categoriesData;
            } else {
                // 加锁失败...自旋重试...
                Thread.sleep(200);
                return this.getCatalogJsonFromDbWithRedisLock();
            }
        } catch (Exception e) {
            log.error("分布式加锁操作失败：", e);
        }

        return null;
    }

    /**
     * 从数据库查询并封装首页分类数据
     *
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        /*    note: 使用类似synchronized的本地同步锁，在分布式场景下是不够完美的，
         *     但是可以使用；
         *     解决：使用分布式锁可以锁住所有服务
         */
        synchronized (this) {
            return this.getCategoriesData();
        }
    }

    private Map<String, List<Catalog2Vo>> getCategoriesData() {
        // 得到锁以后，应该再去缓存中确定是否有数据，如果没有再去数据库查
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isNotBlank(catalogJson)) {
            Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson,
                    new TypeReference<Map<String, List<Catalog2Vo>>>() {
                    });
            return result;
        }
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
        // 将查到的数据再次存入缓存
        stringRedisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(map),
                1, TimeUnit.DAYS);
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