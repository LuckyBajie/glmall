package com.glmall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.glmall.product.entity.AttrGroupEntity;
import com.glmall.product.entity.BrandEntity;
import com.glmall.product.service.AttrGroupService;
import com.glmall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
class GlmallProductApplicationTests {

    @Resource
    private BrandService brandService;

    @Resource
    private AttrGroupService attrGroupService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
