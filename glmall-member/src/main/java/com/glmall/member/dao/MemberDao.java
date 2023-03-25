package com.glmall.member.dao;

import com.glmall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-25 17:42:30
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
