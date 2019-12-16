package com.dongtian.epos.repository;

import com.alibaba.fastjson.JSONObject;
import com.dongtian.epos.entity.ProductInfo;
import com.dongtian.epos.utils.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductInfoTest {
    @Autowired
    private ProductInfoRepository productInfoRepository;
    @Test
    public void saveTest(){
        ProductInfo productInfo = new ProductInfo();
        productInfo.setCreateTime( DateUtils.getDate() );
        productInfo.setUpdateTime( DateUtils.getDate() );
        productInfo.setProductId( "3" );
        productInfo.setProductName( "莲子粥" );
        productInfo.setProductIcon("http://xxx.jpg");
        productInfo.setProductDescription("物美价廉");
        productInfo.setProductStock( 100 );
        productInfo.setProductStatus( 0 );
        productInfo.setProductPrice(new BigDecimal(5.0) );
        productInfo.setCategoryType( 2 );
        productInfoRepository.save( productInfo );

    }
}
