package com.dongtian.epos.service.impl;

import com.dongtian.epos.dto.CartDto;
import com.dongtian.epos.entity.ProductInfo;
import com.dongtian.epos.enums.ProductStatusEnum;
import com.dongtian.epos.enums.ResultEnum;
import com.dongtian.epos.exceptions.ParamException;
import com.dongtian.epos.repository.ProductInfoRepository;
import com.dongtian.epos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductInfoRepository productRepository;
    @Override
    public ProductInfo findOne(String productId) {
        return productRepository.getOne( productId );
    }

    @Override
    public List<ProductInfo> findUpAll() {
        return productRepository.findByProductStatus( ProductStatusEnum.UP.getCode());
    }

    @Override
    public Page<ProductInfo> findAll(Pageable pageable) {
        return productRepository.findAll( pageable );
    }

    @Override
    public ProductInfo save(ProductInfo productInfo) {
        return productRepository.save( productInfo );
    }

    @Override
    @Transactional
    public void increaseStock(List<CartDto> cartDtoList) {

        for (CartDto cartDto:cartDtoList) {
            String productId = cartDto.getProductId();
            //通过id查商品
            ProductInfo productInfo = productRepository.getOne( productId );
            if (productInfo == null) {
                throw new ParamException( ResultEnum.PRODUCT_NOT_EXIST);
            }
            //拿到商品查库存并 + 购物车中的数量
            Integer stock = productInfo.getProductStock() + cartDto.getProductQuantity();
            //设置新库存
            productInfo.setProductStock(stock);

            productRepository.save(productInfo);
        }
    }

    @Override
    @Transactional
    public void decreaseStock(List<CartDto> cartDtoList) {
        //遍历cartDTOList，拿到商品id 和 数量
        for (CartDto cartDto:cartDtoList) {
            String productId = cartDto.getProductId();
            ProductInfo productInfo = productRepository.getOne( productId );
            if (productInfo == null) {
                throw new ParamException( ResultEnum.PRODUCT_NOT_EXIST);
            }
            Integer stock = productInfo.getProductStock() - cartDto.getProductQuantity();
            if (stock < 0) {
                throw new ParamException(ResultEnum.PRODUCT_STOCK_ERROR);
            }
            productInfo.setProductStock(stock);

            productRepository.save(productInfo);
        }
    }

    //上架
    @Override
    public ProductInfo onSale(String productId) {
        return null;
    }
    //下架
    @Override
    public ProductInfo offSale(String productId) {
        return null;
    }
}
