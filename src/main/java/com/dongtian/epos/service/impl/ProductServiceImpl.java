package com.dongtian.epos.service.impl;

import com.dongtian.epos.entity.ProductInfo;
import com.dongtian.epos.enums.ProductStatusEnum;
import com.dongtian.epos.repository.ProductInfoRepository;
import com.dongtian.epos.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
