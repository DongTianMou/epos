package com.dongtian.epos.repository;

import com.alibaba.fastjson.JSONObject;
import com.dongtian.epos.entity.ProductCategory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductCategoryTest {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Test
    public void saveTest(){
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategoryName( "河粉" );
        productCategory.setCategoryType( 1 );
        productCategory.setCreateTime(new Date() );
        productCategory.setUpdateTime( new Date(  ) );
        productCategoryRepository.save( productCategory );
    }

    @Test
    public void getOneTest(){
        ProductCategory productCategory = productCategoryRepository.getOne( 1 );
        int type = productCategory.getCategoryType();
    }

    @Test
    public void findByCategoryTypeInTest() {
        List<Integer> list = Arrays.asList(1,2,3);
        List<ProductCategory> result = productCategoryRepository.findByCategoryTypeIn(list);
        Assert.assertNotEquals(0, result.size());
        System.out.println(result);
    }
}
