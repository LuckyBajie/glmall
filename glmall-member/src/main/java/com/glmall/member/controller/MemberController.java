package com.glmall.member.controller;

import com.glmall.common.constant.BusinessErrorCodeEnum;
import com.glmall.common.to.MemberUserRegisterTo;
import com.glmall.common.to.UserLoginTo;
import com.glmall.common.utils.PageUtils;
import com.glmall.common.utils.R;
import com.glmall.member.entity.MemberEntity;
import com.glmall.member.exceptions.PhoneExistException;
import com.glmall.member.exceptions.UserNameExistException;
import com.glmall.member.feign.CouponCallService;
import com.glmall.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;


/**
 * 会员
 *
 * @author lifeifei
 * @email lifeifei@gmail.com
 * @date 2023-03-25 17:42:30
 */
@Slf4j
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponCallService couponCallService;


    @RequestMapping("/openfeignTest")
    public R openfeignTest() {
        log.info("调用coupon服务，发起请求");
        return couponCallService.openFeignTest();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 会员注册
     */
    @PostMapping("/userReg")
    public R userReg(@RequestBody MemberUserRegisterTo vo) {
        try {
            memberService.registerUser(vo);
        } catch (PhoneExistException e) {
            return R.error(BusinessErrorCodeEnum.PHONE_NUMBER_EXIST_EXCEPTION);
        } catch (UserNameExistException e) {
            return R.error(BusinessErrorCodeEnum.USER_EXIST_EXCEPTION);
        } catch (Exception e) {
            return R.error();
        }

        return R.ok();
    }

    @PostMapping("/userLogin")
    public R userLogin(@RequestBody UserLoginTo to) {

        MemberEntity entity = memberService.login(to);

        return entity == null ? R.error(BusinessErrorCodeEnum.USER_LOGIN_EXCEPTION) : R.ok();
    }

}
