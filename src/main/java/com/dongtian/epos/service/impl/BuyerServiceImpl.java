package com.dongtian.epos.service.impl;

import com.dongtian.epos.dto.OrderDto;
import com.dongtian.epos.enums.ResultEnum;
import com.dongtian.epos.exceptions.ParamException;
import com.dongtian.epos.service.BuyerService;
import com.dongtian.epos.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class BuyerServiceImpl implements BuyerService {
    @Autowired
    private OrderService orderService;
    @Override
    public OrderDto findOrderOne(String openid, String orderId) {
        //根据orderId判断是不是有订单
        //根据openid判断是否是自己的订单
        return CheckOrderAndSelf( openid,orderId );
    }

    @Override
    public OrderDto cancelOrder(String openid, String orderId) {
        //根据orderId判断是不是有订单
        //根据openid判断是否是自己的订单
        OrderDto orderDto = CheckOrderAndSelf( openid,orderId );
        if (orderDto == null) {
            log.error("查不到改订单, orderId={}", orderId);
            throw new ParamException(ResultEnum.ORDER_NOT_EXIST);
        }
        return orderService.cancel( orderDto );
    }

    private OrderDto CheckOrderAndSelf(String openid, String orderId){
        //根据orderId判断是不是有订单
        OrderDto orderDto = orderService.findOneByOrderID( orderId );
        if (orderDto==null){
            return null;
        }
        //根据openid判断是否是自己的订单
        if (!orderDto.getBuyerOpenid(  ).equals( openid )){
            log.error("openId不一致，查看订单详情识别");
            throw new ParamException( ResultEnum.OPENID_ERROR );
        }
        return orderDto;
    }
}
