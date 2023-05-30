package com.glmall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UserRegisterVo {

    @NotEmpty(message = "用户名必须提交")
    @Length(min = 6, max = 18, message = "用户名必须是6-18位")
    private String name;

    @NotEmpty(message = "密码必须提交")
    @Length(min = 6, max = 18, message = "密码必须是6-18位")
    private String password;

    //    @Pattern(regexp = "^[1][0-9]{10}$")
    @NotEmpty(message = "手机号码必须提交")
    @Pattern(regexp = "^[1][3-9][0-9]{9}$", message = "手机号格式不正确")
    private String phone;

    @NotEmpty(message = "验证码必须提交")
    @Pattern(regexp = "^[0-9]{6}$", message = "验证码必须是6位数字")
    private String code;
}
