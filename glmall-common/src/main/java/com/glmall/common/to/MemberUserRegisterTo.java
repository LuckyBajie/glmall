package com.glmall.common.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberUserRegisterTo {

    private String name;

    private String password;

    private String phone;

}
