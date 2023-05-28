package com.glmall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.glmall.product.dao.AttrGroupDao;
import com.glmall.product.entity.AttrGroupEntity;
import com.glmall.product.entity.BrandEntity;
import com.glmall.product.service.AttrGroupService;
import com.glmall.product.service.BrandService;
import com.glmall.product.service.SkuSaleAttrValueService;
import com.glmall.product.web.vo.ItemSaleAttrsVo;
import com.glmall.product.web.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
class GlmallProductApplicationTests {

    @Resource
    private BrandService brandService;

    @Resource
    private AttrGroupService attrGroupService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    AttrGroupDao attrGroupDao;

    @Resource
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Test
    void testAttrGroupDao(){
        List<SpuItemAttrGroupVo> vos = attrGroupDao.getAttrGroupWithAttrsBySpuId(60l, 225l);

        log.info(vos.toString());

        List<ItemSaleAttrsVo> vos1 = skuSaleAttrValueService.getSaleAttrsBySpuId(6l);
        log.info(vos1.toString());


    }

    @Test
    void testRedissonWRLock(){
        String uuid = UUID.randomUUID().toString();
    }

    @Test
    void testRedisson() throws InterruptedException {
        System.out.println(redissonClient);
        // 获取锁
        RLock anyLock = redissonClient.getLock("anyLock");
        // note: 加锁，拿不到锁，会阻塞等待
        //  1）如果不加超时时间，如果业务超长，redisson的【看门狗】会自动为
        //  该锁续期，不用担心因为业务超长导致锁自动过期；
        //  2）加锁的业务只要运行完成，就不会给当前锁续期，即使不手动释放，
        //  锁默认30s后自动释放；
        // anyLock.lock();
        // note:指定锁超时时间，则看门狗【不会自动续期】，锁到时间会自动释放
        //  锁超时后，当前线程不能再调用unlock释放锁，否则会报错
         anyLock.lock(10, TimeUnit.SECONDS);
        try {
            Thread.sleep(30000);
            System.out.println("加锁成功，执行业务:"+ Thread.currentThread().getId());
        }finally {
            // 解锁，如果锁提前超时了，释放未被持有的锁，则会报错
            anyLock.unlock();
        }
    }
    @Test
    void testRedis(){
        ValueOperations<String, String> ops = stringRedisTemplate
                .opsForValue();
        ops.set("hello","world_"+ UUID.randomUUID());

        String hello = ops.get("hello");
        System.out.println(hello);
    }

    @Test
    void testNothing(){
        System.out.println("infoById");
    }

    @Test
    void testFindPath(){
        AttrGroupEntity infoById = attrGroupService.getInfoById(225l);
        System.out.println(infoById);
    }

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setName("华为");
        brandService.save(brandEntity);

        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach((item) -> {
            System.out.println(item);
        });

        System.out.println("----------输出完毕-----------");
    }

}
