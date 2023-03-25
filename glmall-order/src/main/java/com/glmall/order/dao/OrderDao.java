package com.glmall.order.dao;

import com.glmall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-25 17:34:27
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
