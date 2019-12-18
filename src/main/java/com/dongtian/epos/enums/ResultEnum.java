package com.dongtian.epos.enums;

import lombok.Getter;

@Getter
public enum ResultEnum {
    PRODUCT_NOT_EXIST(10,"商品不存在"),
    PRODUCT_STOCK_ERROR(11, "商品库存不足"),
    ORDER_NOT_EXIST(12,"订单不存在"),
    ORDER_DETAIL_NOT_EXIST(13,"订单详情为空"),
    ORDER_STATUS_ERROR(14,"订单状态错误"),
    ORDER_STATUS_UPDATE_ERROR(15,"订单状态更新失败"),
    ORDER_DETAIL_EMPTY(16,"订单详情为空"),
    ORDER_PAY_STATUS_UPDATE_ERROR(17,"订单支付状态更改失败"),
    ORDER_PAY_STATUS_ERROR(18,"支付状态错误"),
    PARAM_CHECK_ERROR(19,"参数校验失败"),
    PARAM_CONVERSION_FAIL(20,"参数转换失败"),
    CART_EMPTY(21,"提交的购物车为空"),
    OPENID_ERROR(22,"openid不一致"),
    ;

    private Integer code;
    private String msg;


    ResultEnum(Integer code,String msg) {
        this.msg = msg;
        this.code = code;
    }
}
