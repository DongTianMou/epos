package com.dongtian.epos.service;

import com.dongtian.epos.dto.OrderDto;

public interface BuyerService {
    /** 买家查询订单*/
    OrderDto findOrderOne(String openid, String orderId);
    /** 买家取消订单*/
    OrderDto cancelOrder(String openid, String orderId);
}
