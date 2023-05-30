package com.glmall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.glmall.common.to.MemberUserRegisterTo;
import com.glmall.common.to.UserLoginTo;
import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.Query;
import com.glmall.member.dao.MemberDao;
import com.glmall.member.entity.MemberEntity;
import com.glmall.member.entity.MemberLevelEntity;
import com.glmall.member.exceptions.PhoneExistException;
import com.glmall.member.exceptions.UserNameExistException;
import com.glmall.member.service.MemberLevelService;
import com.glmall.member.service.MemberService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Resource
    private MemberLevelService memberLevelService;

    @Override
    public void registerUser(MemberUserRegisterTo vo) {
        MemberEntity entity = new MemberEntity();
        // 检查用户名和手机号是否唯一，使用异常机制
        this.checkColumnExist("username", vo.getName(), new UserNameExistException());
        this.checkColumnExist("mobile", vo.getPhone(), new PhoneExistException());

        entity.setUsername(vo.getName());
        entity.setMobile(vo.getPhone());
        // todo 密码需要md5加盐存储
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        entity.setPassword(encode);

        // 设置默认初始值
        MemberLevelEntity defaultLevel = memberLevelService.getDefualtLevelId();
        entity.setLevelId(defaultLevel.getId());
        entity.setCreateTime(new Date());
        this.save(entity);
    }

    @Override
    public MemberEntity login(UserLoginTo to) {
        String loginAccount = to.getLoginAccount();
        String password = to.getPassword();

        MemberEntity one = this.getOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginAccount).or()
                .eq("mobile", loginAccount));

        return (one != null && new BCryptPasswordEncoder().matches(password, one.getPassword()))
                ? one : null;
    }

    private void checkColumnExist(String column, String value, RuntimeException e) {
        long count = this.count(new QueryWrapper<MemberEntity>()
                .eq(column, value));
        if (count > 0) {
            throw e;
        }
    }

}