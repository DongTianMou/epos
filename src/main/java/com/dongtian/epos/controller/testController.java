package com.dongtian.epos.controller;

import com.alibaba.fastjson.JSONObject;
import com.dongtian.epos.dto.OrderDto;
import com.dongtian.epos.entity.OrderDetail;
import com.dongtian.epos.entity.ProductCategory;
import com.dongtian.epos.repository.ProductCategoryRepository;
import com.dongtian.epos.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test")
@Slf4j
public class testController {
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private OrderService orderService;

    private final String BUYER_OPENID = "1101110";

    private final String ORDER_ID = "1576552010818661913";

    @RequestMapping("/update")
    public String updateTest() {
        ProductCategory result = productCategoryRepository.getOne( 1);
        result.setCategoryType( 1 );
        productCategoryRepository.save( result );
        return JSONObject.toJSONString( result );
    }

    @RequestMapping("/create")
    public String createTest() {
        OrderDto orderDTO = new OrderDto();
        orderDTO.setBuyerName("林东填");
        orderDTO.setBuyerAddress("深圳");
        orderDTO.setBuyerPhone("123456789012");

        orderDTO.setBuyerOpenid(BUYER_OPENID);

        //购物车
        List<OrderDetail> orderDetailList = new ArrayList<>();
        OrderDetail o1 = new OrderDetail();
        o1.setProductId("1");
        o1.setProductQuantity(1);

        OrderDetail o2 = new OrderDetail();
        o2.setProductId("2");
        o2.setProductQuantity(2);

        orderDetailList.add(o1);
        orderDetailList.add(o2);

        orderDTO.setOrderDetailList(orderDetailList);

        OrderDto result = orderService.create(orderDTO);
        log.info("【创建订单】result={}", result);
        return JSONObject.toJSONString( result );
    }

    @RequestMapping("/findOne")
    public String findOne() {
        OrderDto result = orderService.findOneByOrderID( ORDER_ID );
        //log.info("【查询单个订单】result={}", result);
        return JSONObject.toJSONString( result );
    }

    @RequestMapping("/findList")
    public String findList() {
        //PageRequest是pageable的实现类，PageRequest.of(0,2)--第几页，一页多少个size
        PageRequest request = PageRequest.of(0,2);
        Page<OrderDto> result = orderService.findList(BUYER_OPENID, request);
        return JSONObject.toJSONString( result );
    }

    @RequestMapping("/cancel")
    public String  cancel() {
        OrderDto orderDto = orderService.findOneByOrderID(ORDER_ID);
        OrderDto result = orderService.cancel(orderDto);
        return JSONObject.toJSONString( result );
    }

    @RequestMapping("/finish")
    public String finish() {
        OrderDto orderDto = orderService.findOneByOrderID(ORDER_ID);
        OrderDto result = orderService.finish(orderDto);
        return JSONObject.toJSONString( result );
    }

    @RequestMapping("/paid")
    public String paid() {
        OrderDto orderDto = orderService.findOneByOrderID(ORDER_ID);
        OrderDto result = orderService.paid(orderDto);
        return JSONObject.toJSONString( result );
    }




}
