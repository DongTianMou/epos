package com.dongtian.epos.controller;

import com.dongtian.epos.converter.OrderFormToOrderDto;
import com.dongtian.epos.dto.OrderDto;
import com.dongtian.epos.enums.ResultEnum;
import com.dongtian.epos.exceptions.ParamException;
import com.dongtian.epos.form.OrderForm;
import com.dongtian.epos.service.BuyerService;
import com.dongtian.epos.service.OrderService;
import com.dongtian.epos.utils.ResultVOUtils;
import com.dongtian.epos.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/buyer/order")
@Slf4j
public class BuyerOrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private BuyerService buyerService;
    /** 创建订单. */
    @PostMapping("/create")
    public ResultVO<Map<String,String>> create(@Valid OrderForm orderForm,
                                               BindingResult bindingResult){
        //判断检验结果是否正确
        if (bindingResult.hasErrors()){
            log.error( "[参数校验失败]，orderForm={}",orderForm );
            throw new ParamException( ResultEnum.PARAM_CHECK_ERROR);
        }
        //create()需要传一个orderDto
        OrderDto orderDto = OrderFormToOrderDto.convert(orderForm);
        //判断订单详情是否为空
        if (CollectionUtils.isEmpty(orderDto.getOrderDetailList())) {
            log.error("[创建订单],提交的购物车不能为空");
            throw new ParamException(ResultEnum.CART_EMPTY);
        }
        //创建订单
        OrderDto orderDtoInfo = orderService.create( orderDto );

        //拿到结果适应返回
        Map<String, String> map = new HashMap<>();
        map.put("orderId", orderDtoInfo.getOrderId());
        return ResultVOUtils.success( map );
    }

    /** 查询订单列表. */
    @GetMapping("/list")
    public ResultVO<List<OrderDto>> list(@RequestParam( value = "openid") String openid,
                                         @RequestParam( value = "page",defaultValue = "0")Integer page,
                                         @RequestParam( value = "size",defaultValue = "5")Integer size){
        //判断openid是否为空
        if (StringUtils.isEmpty( openid )){
            log.error( "[查询订单列表]--openid为空" );
            throw new ParamException( ResultEnum.PARAM_CHECK_ERROR);
        }
        //构造pageable对象
        PageRequest pageRequest = PageRequest.of( page,size);
        //调用事务层的findList()
        Page<OrderDto> orderDtoList = orderService.findList( openid,pageRequest );

        return ResultVOUtils.success(orderDtoList.getContent());

    }

    /** 查询单个订单详情*/
    @GetMapping("/detail")
    public ResultVO<OrderDto> orderDetail(@RequestParam( value = "openid") String openid,
                                         @RequestParam( value = "orderId") String orderId){
        //判断openid是否为空
        if (StringUtils.isEmpty( openid )){
            log.error( "[查询订单详情]--openid为空" );
            throw new ParamException( ResultEnum.PARAM_CHECK_ERROR);
        }

        //判断orderId是否为空
        if (StringUtils.isEmpty( openid )){
            log.error( "[查询订单详情]--orderId为空" );
            throw new ParamException( ResultEnum.PARAM_CHECK_ERROR);
        }

        OrderDto orderDto = buyerService.findOrderOne(openid, orderId );
        return ResultVOUtils.success(orderDto);
    }

    /** 取消订单. */
    @PostMapping("/cancel")
    public ResultVO cancel(@RequestParam("openid") String openid,
                           @RequestParam("orderId") String orderId) {
        buyerService.cancelOrder( openid,orderId );
        return ResultVOUtils.success();
    }
}
