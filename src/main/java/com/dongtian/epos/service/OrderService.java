package com.dongtian.epos.service;

import com.dongtian.epos.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    /** 创建订单. */
    OrderDto create(OrderDto orderDto);
    /** 查询单个订单. */
    OrderDto findOneByOrderID(String orderId);
    /** 查询订单列表. */
    Page<OrderDto> findList(String buyerOpenid, Pageable pageable);
    /** 取消订单. */
    OrderDto cancel(OrderDto orderDto);
    /** 完结订单. */
    OrderDto finish(OrderDto orderDTO);
    /** 支付订单. */
    OrderDto paid(OrderDto orderDTO);
    /** 查询订单列表. */
    Page<OrderDto> findList(Pageable pageable);

}
