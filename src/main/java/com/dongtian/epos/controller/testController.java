package com.dongtian.epos.controller;

import com.alibaba.fastjson.JSONObject;
import com.dongtian.epos.entity.ProductCategory;
import com.dongtian.epos.repository.ProductCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sell/test")
public class testController {
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @RequestMapping("/update")
    public String updateTest() {
        ProductCategory result = productCategoryRepository.getOne( 1);
        result.setCategoryType( 1 );
        productCategoryRepository.save( result );
        return JSONObject.toJSONString( result );
    }

}
