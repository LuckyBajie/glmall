package com.glmall.order;

import com.glmall.order.entity.OrderEntity;
import com.glmall.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
class GlmallOrderApplicationTests {

    @Resource 
    OrderService orderService;

    @Test
    void contextLoads() {

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn("123456");
        orderService.save(orderEntity);

        List<OrderEntity> list = orderService.list();
        list.forEach((item)->{
            System.out.println(item);
        });

        System.out.println("-----------执行完毕------------");
    }

}
