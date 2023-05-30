package com.glmall.auth.web.controller;

import com.alibaba.fastjson.TypeReference;
import com.glmall.auth.feign.MemberFeignService;
import com.glmall.auth.feign.ThirdPartyFeignService;
import com.glmall.auth.vo.UserRegisterVo;
import com.glmall.common.constant.AuthConstant;
import com.glmall.common.constant.BusinessErrorCodeEnum;
import com.glmall.common.to.MemberUserRegisterTo;
import com.glmall.common.to.UserLoginTo;
import com.glmall.common.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Controller
public class AuthController {

    @Resource
    private ThirdPartyFeignService thirdPartyFeignService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private MemberFeignService memberFeignService;

    /**
     * 发送短信验证码
     *
     * @param phoneNum
     * @return
     */
    @ResponseBody
    @GetMapping({"/sms/send", "/"})
    public R sendCode(@RequestParam("phoneNum") String phoneNum) {
        //1. 接口防刷:防止同一个手机号60s内，重复发送
        String redisKey = AuthConstant.SMS_CODE_CACHE_PREFIX + phoneNum;
        String redisCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isNotBlank(redisCode)) {
            Long redisTime = Long.valueOf(redisCode.split("_")[1]);

            if (System.currentTimeMillis() - redisTime < 60000 * 10) {
                // 60s内不能再发
                return R.error(BusinessErrorCodeEnum.AUTH_REG_SMS_EXCEPTION);
            }
        }

        //2. 验证码的再次校验，redis保存
        // 随机六位正整数
        String code = "" + (100000 + new Random().nextInt(899999));
        stringRedisTemplate.opsForValue().set(redisKey, code + "_" + System.currentTimeMillis(), 10, TimeUnit.MINUTES);

        // todo 这里为了省钱，就不真的发到手机了，自己去redis看去吧
//        return thirdPartyFeignService.sendSms(phoneNum, code);
        return R.ok();
    }

    //注册成功，回到登录页
    @PostMapping({"/register"})
    public String register(@Valid UserRegisterVo vo, BindingResult result, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        // 参数校验出错，转发到注册页
        String res = validateParams(result, redirectAttributes);
        if (StringUtils.isNotBlank(res)) {
            return res;
        }

        // 调用远程服务，进行注册
        // 1.校验验证码
        String code = vo.getCode();
        String phoneNum = vo.getPhone();
        String redisCodeKey = AuthConstant.SMS_CODE_CACHE_PREFIX + phoneNum;
        String redisCode = stringRedisTemplate.opsForValue().get(redisCodeKey);

        if (StringUtils.isBlank(redisCode)) {
            return redirect2RegWhenCodeValidFail("code", "验证码校验错误",
                    "redirect:http://auth.glmall.com/reg.html", redirectAttributes);
        }

        if (!redisCode.contains("_") || !code.equals(redisCode.split("_")[0])) {
            return redirect2RegWhenCodeValidFail("code", "验证码校验错误",
                    "redirect:http://auth.glmall.com/reg.html", redirectAttributes);
        }

        //2. 验证码校验通过后，需要删除redis的验证码，然后调用远程服务进行注册
        stringRedisTemplate.delete(redisCodeKey);
        MemberUserRegisterTo vo1 = new MemberUserRegisterTo();
        BeanUtils.copyProperties(vo, vo1);
        vo1.setName(vo.getName());
        R r = memberFeignService.userReg(vo1);
        if (r.getCode() != 0) {
            String msg = r.getDataByKey("msg", new TypeReference<String>() {
            });
            return redirect2RegWhenCodeValidFail("msg", msg,
                    "redirect:http://auth.glmall.com/reg.html", redirectAttributes);
        }

        // 注册成功，重定向到登录页
        return "redirect:http://auth.glmall.com/login.html";
    }

    private String redirect2RegWhenCodeValidFail(String errKey, String errValue,
                                                 String redirectUrl,
                                                 RedirectAttributes redirectAttributes) {
        Map<String, String> errors = new HashMap<>();
        errors.put(errKey, errValue);
        redirectAttributes.addFlashAttribute("errors", errors);
        return redirectUrl;
    }

    private String validateParams(BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(fieldError ->
                    errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
            redirectAttributes.addFlashAttribute("errors", errors);

            /*
             note: HttpRequestMethodNotSupportedException: Request method 'POST' not supported
                    我们之前在viewController做了/reg.html的注册映射，这个注册映射
                    默认请求是get请求，当我们在这里转发使用了post转发的时候，将会
                    报上述异常
             */
            // return "forward:/reg.html";
            // note: 转发的方式，地址栏地址不会变，但是可以通过model传递参数
            // return "reg";
            /*
             note: 重定向的方式，地址栏地址会变，但是需要 redirectAttributes 给页面传递参数
                      利用session原理，将数据放在session中，只要跳到下一个页面取出
                      数据后，session就会被删除
             */
            return "redirect:http://auth.glmall.com/reg.html";
        }
        return null;
    }

    @PostMapping({"/login"})
    public String login(UserLoginTo to, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        R r = memberFeignService.userLogin(to);
        if (r.getCode()==0) {
            return "redirect:http://glmall.com";
        }
        String msg = r.getDataByKey("msg", new TypeReference<String>() {
        });
        return redirect2RegWhenCodeValidFail("msg", msg,
                "redirect:http://auth.glmall.com/login.html", redirectAttributes);
    }


    /*
    下面两个简单的跳转页面的方法，已经配置在
    glmall-common包的WebMvcConfiguration#addViewControllers
    方法中了，不用每次都写这种【空方法了】
    @GetMapping({"/login.html","/"})
    public String loginPage(Model model, HttpServletRequest request){
        return "login";
    }

    @GetMapping({"/reg.html"})
    public String regPage(Model model, HttpServletRequest request){
        return "reg";
    }*/
}
