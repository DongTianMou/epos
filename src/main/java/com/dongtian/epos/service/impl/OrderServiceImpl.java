package com.dongtian.epos.service.impl;

import com.dongtian.epos.converter.OrderMasterToOrderDto;
import com.dongtian.epos.dto.CartDto;
import com.dongtian.epos.dto.OrderDto;
import com.dongtian.epos.entity.OrderDetail;
import com.dongtian.epos.entity.OrderMaster;
import com.dongtian.epos.entity.ProductInfo;
import com.dongtian.epos.enums.OrderStatusEnum;
import com.dongtian.epos.enums.PayStatusEnum;
import com.dongtian.epos.enums.ResultEnum;
import com.dongtian.epos.exceptions.ParamException;
import com.dongtian.epos.repository.OrderDetailRepository;
import com.dongtian.epos.repository.OrderMasterRepository;
import com.dongtian.epos.service.OrderService;
import com.dongtian.epos.service.ProductService;
import com.dongtian.epos.utils.DateUtils;
import com.dongtian.epos.utils.GetIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
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
                throw new ParamException( ResultEnum.PRODUCT_NOT_EXIST);
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
            orderDetail.setCreateTime(DateUtils.getDate());
            orderDetail.setUpdateTime( DateUtils.getDate());
            orderDetailRepository.save(orderDetail);
        }
        //4. 写入订单数据库（orderMaster)
        OrderMaster orderMaster = new OrderMaster();
        //将orderDto中的属性复制到orderMaster（多到少），相同属性会覆盖
        orderDto.setOrderId( orderId );
        BeanUtils.copyProperties(orderDto, orderMaster);
        //属性需要再设置
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
    public OrderDto findOneByOrderID(String orderId) {
        //通过orderId查找 orderMaster，orderDetailList
        OrderMaster orderMaster = orderMasterRepository.getOne( orderId );
        if (orderMaster == null){
            log.error("订单不存在");
            throw new ParamException( ResultEnum.ORDER_NOT_EXIST);
        }
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId( orderId );
        if (CollectionUtils.isEmpty(orderDetailList)){
            log.error("订单为空");
            throw new ParamException( ResultEnum.ORDER_DETAIL_NOT_EXIST);
        }
        //将orderMaster属性赋值到orderDto
        OrderDto orderDto = OrderMasterToOrderDto.convert( orderMaster);
        //把orderDetailList设置仅orderDto
        orderDto.setOrderDetailList(orderDetailList);
        return orderDto;
    }

    @Override
    public Page<OrderDto> findList(String openid, Pageable pageable) {
        //通过openid查找 orderMasterPage
        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByBuyerOpenid(openid,pageable);
        //orderMasterPage转为OrderDtoList,点单列表就不包括订单详情了
        List<OrderDto> orderDtoList = OrderMasterToOrderDto.convert(orderMasterPage.getContent());
        return new PageImpl<OrderDto>(orderDtoList,pageable,orderMasterPage.getTotalElements());
    }

    @Override
    @Transactional
    public OrderDto cancel(OrderDto orderDto) {
        //判断订单状态：是否为未完成订单
        if (!orderDto.getOrderStatus().equals( OrderStatusEnum.NEW.getCode())){
            log.info( "[取消订单]--订单状态错误，不是新订单,orderId={}, orderStatus={}", orderDto.getOrderId(), orderDto.getOrderStatus());
            throw new ParamException(ResultEnum.ORDER_STATUS_ERROR);
        }
        //修改订单状态为取消状态
        OrderMaster orderMaster = new OrderMaster();
        orderDto.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        BeanUtils.copyProperties(orderDto, orderMaster);
        orderMaster.setOrderStatus( OrderStatusEnum.CANCEL.getCode());
        orderMaster.setUpdateTime( DateUtils.getDate() );
        OrderMaster updateOrder = orderMasterRepository.save(orderMaster);
        if (updateOrder==null){
            log.info( "[取消订单]--订单状态更新错误, orderMaster={}", orderMaster);
            throw new ParamException(ResultEnum.ORDER_STATUS_UPDATE_ERROR);
        }
        //增加库存
        if (CollectionUtils.isEmpty(orderDto.getOrderDetailList())) {
            log.error("[取消订单]--订单中无商品详情, orderDto={}", orderDto);
            throw new  ParamException(ResultEnum.ORDER_DETAIL_EMPTY);
        }
        List<CartDto> cartDtoList = orderDto.getOrderDetailList().stream()
                .map( e-> new CartDto( e.getProductId(),e.getProductQuantity() ) )
                .collect( Collectors.toList());
        productService.increaseStock( cartDtoList );
        //如果已经支付，退款
        //TODO
        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto finish(OrderDto orderDto) {
        //判断订单状态
        if( !orderDto.getOrderStatus().equals( OrderStatusEnum.NEW.getCode())) {
            log.error( "[完结订单]--订单状态不正确或订单未支付，orderId={},orderStatus={}", orderDto.getOrderId(), orderDto.getOrderStatus() );
            throw new ParamException( ResultEnum.ORDER_STATUS_ERROR );
        }
        //判断订单支付状态
        if (orderDto.getPayStatus().equals(PayStatusEnum.WAIT.getCode())) {
            log.error("[完结订单]--订单未支付 orderPayStatus={}", orderDto.getPayStatus());
            throw new ParamException(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }
        //修改订单状态
        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties( orderDto,orderMaster );
        orderMaster.setUpdateTime( DateUtils.getDate() );
        orderMaster.setOrderStatus( OrderStatusEnum.FINISHED.getCode() );
        OrderMaster updateOrder = orderMasterRepository.save(orderMaster);
        if (updateOrder==null){
            log.info( "[完结订单]--订单状态更新错误, orderStatus={}", orderMaster.getOrderStatus());
            throw new ParamException(ResultEnum.ORDER_STATUS_UPDATE_ERROR);
        }
        return orderDto;
    }

    @Override
    @Transactional
    public OrderDto paid(OrderDto orderDto) {
        //判断订单状态
        if(!orderDto.getOrderStatus().equals( OrderStatusEnum.NEW.getCode() )){
            log.error( "[支付订单]--订单状态不正确，orderId={},orderStatus={}",orderDto.getOrderId(),orderDto.getOrderStatus() );
            throw new ParamException( ResultEnum.ORDER_STATUS_ERROR);
        }
        //判断订单支付状态
        if (!orderDto.getPayStatus().equals(PayStatusEnum.WAIT.getCode())) {
            log.error("[支付订单]--订单支付状态不正确, orderDto={}", orderDto);
            throw new ParamException(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }

        //修改订单支付状态
        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties( orderDto,orderMaster );
        orderMaster.setUpdateTime( DateUtils.getDate() );
        orderMaster.setPayStatus( PayStatusEnum.SUCCESS.getCode() );
        OrderMaster updateOrder = orderMasterRepository.save(orderMaster);
        if (updateOrder==null){
            log.info( "[支付订单]--订单支付状态更新错误, orderStatus={}", orderMaster.getOrderStatus());
            throw new ParamException(ResultEnum.ORDER_PAY_STATUS_UPDATE_ERROR);
        }
        return orderDto;
    }

    @Override
    public Page<OrderDto> findList(Pageable pageable) {
        return null;
    }
}
