package com.dongtian.epos.controller;

import com.alibaba.fastjson.JSONObject;
import com.dongtian.epos.dto.OrderDto;
import com.dongtian.epos.entity.OrderDetail;
import com.dongtian.epos.entity.ProductCategory;
import com.dongtian.epos.repository.ProductCategoryRepository;
import com.dongtian.epos.service.OrderService;
import com.dongtian.epos.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final String ORDER_ID = "1497183332311989948";

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

        orderDetailList.add(o1);

        orderDTO.setOrderDetailList(orderDetailList);

        OrderDto result = orderService.create(orderDTO);
        log.info("【创建订单】result={}", result);
        return JSONObject.toJSONString( result );
    }


}
