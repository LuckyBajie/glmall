package com.glmall.common.exception;

import com.glmall.common.constant.BusinessErrorCodeEnum;
import com.glmall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一异常处理类
 * 官方给我们提供了一个@RestControllerAdvice来增强所有@RestController，
 * 然后使用@ExceptionHandler注解，就可以拦截到对应的异常。
 * <p>
 * 1.@RestControllerAdvice(basePackages = {"com.bugpool.leilema"})自动扫描
 * 了所有指定包下的controller，在Response时进行统一处理
 *
 * @RestControllerAdvice： 组合了 @ControllerAdvice 和 @ResponseBody
 */

//@RestControllerAdvice(basePackages = {"com.glmall.**.controller"}) 通配符的写法有问题，没有正确对方法进行增强
@RestControllerAdvice(basePackages = {"com.glmall.coupon.controller",
        "com.glmall.member.controller", "com.glmall.order.controller",
        "com.glmall.product.controller", "com.glmall.ware.controller", "com.glmall.thirdparty.controller"})
@Slf4j
public class GlmallExceptionControllerAdvice {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public R controllerException(MethodArgumentNotValidException e) {
        log.error("controller 出现异常：", e);
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> map = new HashMap<>();
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().stream()
                    .forEach(fieldError -> map.put(fieldError.getField(), fieldError.getDefaultMessage()));
            // .collect(Collectors.toMap(fieldError -> fieldError.getField(), fieldError -> fieldError.getDefaultMessage()));
            return R.error(BusinessErrorCodeEnum.PARAMETER_VALIDATE_EXCEPTION).put("err", map);
        }

        return R.error(BusinessErrorCodeEnum.UNKNOWN_EXCEPTION)
                .put("errMsg", e.getMessage()).put("errClassName", e.getClass().getName());
    }

    @ExceptionHandler({Throwable.class})
    public R handleException(Throwable t) {
        log.error("全局异常捕获：", t);
        return R.error(BusinessErrorCodeEnum.UNKNOWN_EXCEPTION)
                .put("errMsg", t.getMessage()).put("errClassName", t.getClass().getName());
    }
}
