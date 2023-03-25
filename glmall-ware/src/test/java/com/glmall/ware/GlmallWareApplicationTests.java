package com.glmall.ware;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.glmall.ware.entity.WareInfoEntity;
import com.glmall.ware.service.WareInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class GlmallWareApplicationTests {

    @Resource
    WareInfoService wareInfoService;

    @Test
    void contextLoads() {
        WareInfoEntity wareInfo = new WareInfoEntity();
        wareInfo.setAddress("北京");
        wareInfoService.save(wareInfo);

        List<WareInfoEntity> list = wareInfoService.list(new QueryWrapper<WareInfoEntity>().eq("id", 1));
        list.forEach((item)->{
            System.out.println(item);
        });

        System.out.println("执行完毕");
    }

}
