package com.glmall.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberUserRegisterVo {

    private String username;

    private String password;

    private String phone;

}
