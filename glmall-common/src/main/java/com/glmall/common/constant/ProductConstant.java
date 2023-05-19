package com.glmall.common.constant;

import lombok.Getter;

public class ProductConstant {

    @Getter
    public enum AttrEnum{
        ATTR_TYPE_BASE(1,"基本属性"),
        ATTR_TYPE_SALE(0,"销售属性"),
        ;
        private Integer code;
        private String msg;

        AttrEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    @Getter
    public enum StatusEnum{
        SPU_NEW(0,"新建"),
        SPU_UP(1,"上架"),
        SPU_DOWN(2,"下架"),
        ;
        private Integer code;
        private String msg;

        StatusEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }
}
