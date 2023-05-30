package com.glmall.common.to;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserLoginTo {

    @NotEmpty(message = "用户名必须提交")
    private String loginAccount;

    @NotEmpty(message = "密码必须提交")
    private String password;

}
