package com.glmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.glmall.common.to.MemberUserRegisterTo;
import com.glmall.common.to.UserLoginTo;
import com.glmall.common.utils.PageUtils;
import com.glmall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-25 17:42:30
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void registerUser(MemberUserRegisterTo vo);

    MemberEntity login(UserLoginTo to);
}

