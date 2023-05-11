package com.glmall.common.constant;

import lombok.Getter;

public class WareConstant {

    @Getter
    public enum PurchaseStatusEnum{
        CREATED(0,"新建"),
        ASSIGNED(1,"已分配"),
        RECEIVED(2,"已领取"),
        FINISH(3,"已完成"),
        HAS_ERROR(4,"有异常"),
        ;
        private Integer statusCode;
        private String msg;

        PurchaseStatusEnum(Integer statusCode, String msg) {
            this.statusCode = statusCode;
            this.msg = msg;
        }
    }

    @Getter
    public enum PurchaseDetailStatusEnum{
        CREATED(0,"新建"),
        ASSIGNED(1,"已分配"),
        BUYING(2,"正在采购"),
        FINISH(3,"已完成"),
        HAS_ERROR(4,"采购失败"),
        ;
        private Integer statusCode;
        private String msg;

        PurchaseDetailStatusEnum(Integer statusCode, String msg) {
            this.statusCode = statusCode;
            this.msg = msg;
        }
    }
}
