package com.glmall.auth.feign;

import com.glmall.common.to.MemberUserRegisterTo;
import com.glmall.common.to.UserLoginTo;
import com.glmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("glmall-member")
public interface MemberFeignService {

    /**
     * note：这个请求头修改为下面的请求：
     *  原因就是前端从页面表单请求发送过来后，后台请求如果直接通过
     *  feign调用@RequestBody接受参数的方法，会报参数转换的错误；
     *  主要我们almall-common中的FeignBasicAuthRequestInterceptor
     *  对请求头和请求体进行了重写导致了这个问题
     * public R userReg(@RequestBody MemberUserRegisterTo vo);
     * @return
     */
    @PostMapping("/member/member/userReg")
    public R userReg(@RequestBody MemberUserRegisterTo vo);

    @PostMapping("/member/member/userLogin")
    public R userLogin(@RequestBody UserLoginTo to);
}
