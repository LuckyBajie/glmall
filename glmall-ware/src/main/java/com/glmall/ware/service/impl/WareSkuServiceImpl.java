package com.glmall.ware.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.glmall.common.to.SkuHasStockTo;
import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.Query;
import com.glmall.common.utils.R;
import com.glmall.ware.dao.WareSkuDao;
import com.glmall.ware.entity.WareSkuEntity;
import com.glmall.ware.feign.ProductFeignService;
import com.glmall.ware.service.WareSkuService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotBlank(skuId)) {
            wrapper.eq("sku_id", skuId);
        }

        if (StringUtils.isNotBlank(wareId)) {
            wrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1 判断如果没有这个库存则新增
        List<WareSkuEntity> list = this.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId)
                .eq("ware_id", wareId));
        if (CollectionUtils.isEmpty(list)) {
            WareSkuEntity entity = new WareSkuEntity();
            entity.setSkuId(skuId);
            entity.setStock(skuNum);
            entity.setWareId(wareId);
            entity.setStockLocked(0);
            // 远程查询sku的名字，失败不回滚
            // todo 还可以用什么办法让异常出现后不回滚？
            try {
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0) {
                    JSONObject skuInfo = JSON.parseObject(JSON.toJSONString(info.get("skuInfo")));
                    String skuName = skuInfo.getString("skuName");
                    entity.setSkuName(skuName);
                }
            } catch (Exception e) {
            }

            this.save(entity);
        } else {
            this.baseMapper.updateStock(skuId, wareId, skuNum);
        }
    }

    /**
     * @param skuIds
     * @return List<SkuHasStockTo> result，没有数据则表明查不到sku库存
     */
    @Override
    public List<SkuHasStockTo> getSkusHasStock(List<Long> skuIds) {
        // 查出所有sku的库存信息
        List<WareSkuEntity> wareSkuEntities = this.list(new QueryWrapper<WareSkuEntity>()
                .in("sku_id", skuIds));

        List<SkuHasStockTo> result = new ArrayList<>();

        if (!CollectionUtils.isEmpty(wareSkuEntities)) {
            // 以skuId进行分组
            Map<Long, List<WareSkuEntity>> map = wareSkuEntities.stream()
                    .collect(Collectors.groupingBy(WareSkuEntity::getSkuId));
            // 计算每个sku是否有库存
            map.keySet().forEach(key -> {
                List<WareSkuEntity> skuEntities = map.get(key);
                SkuHasStockTo vo = new SkuHasStockTo();
                vo.setHasStock(false);
                if (!CollectionUtils.isEmpty(skuEntities)) {
                    Long stockSum = skuEntities.stream()
                            .mapToLong(skuEntity -> skuEntity.getStock() == null ? 0 : skuEntity.getStock()).sum();
                    Long stockLockedSum = skuEntities.stream()
                            .mapToLong(skuEntity -> skuEntity.getStockLocked() == null ? 0 : skuEntity.getStockLocked()).sum();
                    vo.setHasStock(stockSum - stockLockedSum > 0);
                }
                vo.setSkuId(key);
                result.add(vo);
            });
        }
        return result;
    }

}