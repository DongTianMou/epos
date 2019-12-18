package com.dongtian.epos.converter;

import com.dongtian.epos.dto.OrderDto;
import com.dongtian.epos.entity.OrderDetail;
import com.dongtian.epos.enums.ResultEnum;
import com.dongtian.epos.exceptions.ParamException;
import com.dongtian.epos.form.OrderForm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
@Slf4j
public class OrderFormToOrderDto {
    public static OrderDto convert(OrderForm orderForm){
        OrderDto orderDto = new OrderDto();
        orderDto.setBuyerName( orderForm.getName() );
        orderDto.setBuyerAddress(orderForm.getAddress());
        orderDto.setBuyerPhone(orderForm.getPhone());
        orderDto.setBuyerOpenid( orderForm.getOpenid() );
        Gson gson = new Gson();

        List<OrderDetail> orderDetailList = new ArrayList<>();
        try {
            orderDetailList = gson.fromJson(orderForm.getItems(),
                    new TypeToken<List<OrderDetail>>() {
                    }.getType());
        } catch (Exception e) {
            log.error("【对象转换】错误, string={}", orderForm.getItems());
            throw new ParamException( ResultEnum.PARAM_CONVERSION_FAIL);
        }
        orderDto.setOrderDetailList(orderDetailList);
        return orderDto;
    }
}
