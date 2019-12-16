package com.dongtian.epos.service.impl;

import com.dongtian.epos.dto.CartDto;
import com.dongtian.epos.dto.OrderDto;
import com.dongtian.epos.entity.OrderDetail;
import com.dongtian.epos.entity.OrderMaster;
import com.dongtian.epos.entity.ProductInfo;
import com.dongtian.epos.enums.OrderStatusEnum;
import com.dongtian.epos.enums.PayStatusEnum;
import com.dongtian.epos.enums.ResultEnum;
import com.dongtian.epos.exceptions.ProductException;
import com.dongtian.epos.repository.OrderDetailRepository;
import com.dongtian.epos.repository.OrderMasterRepository;
import com.dongtian.epos.service.OrderService;
import com.dongtian.epos.service.ProductService;
import com.dongtian.epos.utils.DateUtils;
import com.dongtian.epos.utils.GetIDUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private OrderMasterRepository orderMasterRepository;

    //加减库存是事务性的，需要加事务
    @Override
    @Transactional
    public OrderDto create(OrderDto orderDto) {
        //为订单生成唯一id
        String orderId = GetIDUtils.getUniqueId();
        //商品总价默认为0
        BigDecimal orderAmount = new BigDecimal( BigInteger.ZERO);
        //1. 查询商品（数量, 价格）:根据orderDto查询订单详情，遍历订单详情中的productId
        List<OrderDetail> orderDetailList = orderDto.getOrderDetailList();
        for (OrderDetail orderDetail: orderDetailList) {
            ProductInfo productInfo = productService.findOne( orderDetail.getProductId());
            //商品不存在,抛异常
            if (productInfo == null) {
                throw new ProductException( ResultEnum.PRODUCT_NOT_EXIST);
            }
            //2. 计算订单总价:这里的商品单价不应该由客户传，需要我们自己去数据库查
            orderAmount = productInfo.getProductPrice()
                    .multiply( new BigDecimal(orderDetail.getProductQuantity()))
                    .add( orderAmount );

            //3. 订单详情入库
            orderDetail.setDetailId( GetIDUtils.getUniqueId() );
            orderDetail.setOrderId( orderId );
            //把productInfo中属性复制到orderDetail中
            BeanUtils.copyProperties(productInfo, orderDetail);
            orderDetailRepository.save(orderDetail);
        }
        //4. 写入订单数据库（orderMaster)
        OrderMaster orderMaster = new OrderMaster();
        //将orderDto中的属性复制到orderMaster（多到少），相同属性会覆盖
        BeanUtils.copyProperties(orderDto, orderMaster);
        //属性需要再设置
        orderMaster.setOrderId( orderId );
        orderMaster.setOrderAmount( orderAmount );
        orderMaster.setOrderStatus( OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus( PayStatusEnum.WAIT.getCode());
        orderMaster.setCreateTime( DateUtils.getDate() );
        orderMaster.setUpdateTime( DateUtils.getDate() );
        orderMasterRepository.save(orderMaster);

        //5. 扣库存:收集购物车对象，遍历购物车中的商品id 和 购买数量
        List<CartDto> cartDtoList = orderDto.getOrderDetailList().stream()
                .map( e->new CartDto(e.getProductId(),e.getProductQuantity()))
                .collect( Collectors.toList() );
        productService.decreaseStock( cartDtoList );

        return orderDto;
    }

    @Override
    public OrderDto findOne(String orderId) {
        return null;
    }

    @Override
    public Page<OrderDto> findList(String buyerOpenid, Pageable pageable) {
        return null;
    }

    @Override
    public OrderDto cancel(OrderDto orderDto) {
        return null;
    }

    @Override
    public OrderDto finish(OrderDto orderDTO) {
        return null;
    }

    @Override
    public OrderDto paid(OrderDto orderDTO) {
        return null;
    }

    @Override
    public Page<OrderDto> findList(Pageable pageable) {
        return null;
    }
}
