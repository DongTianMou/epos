package com.dongtian.epos.controller;

import com.dongtian.epos.entity.ProductCategory;
import com.dongtian.epos.entity.ProductInfo;
import com.dongtian.epos.repository.ProductCategoryRepository;
import com.dongtian.epos.repository.ProductInfoRepository;
import com.dongtian.epos.service.CategoryService;
import com.dongtian.epos.service.ProductService;
import com.dongtian.epos.utils.ResultVOUtils;
import com.dongtian.epos.vo.ProductInfoVO;
import com.dongtian.epos.vo.ProductVO;
import com.dongtian.epos.vo.ResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/buyer/product")
public class BuyerProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public ResultVO list() {
        //1. 查询所有的上架商品
        List<ProductInfo> productInfoList = productService.findUpAll();
        //2. 查询类目(一次性查询): 拿到type,通过type查询ProductCategory
        List<Integer> categoryTypeList = productInfoList.stream()
                .map( e->e.getCategoryType() )
                .collect( Collectors.toList() );
        List<ProductCategory> productCategoryList = categoryService.findByCategoryTypeIn( categoryTypeList );
        //3. 数据拼装
        List<ProductVO> productVOList = new ArrayList<>();
        for (ProductCategory productCategory: productCategoryList) {
            //设置productVO,类目设置
            ProductVO productVO = new ProductVO();
            productVO.setCategoryType(productCategory.getCategoryType());
            productVO.setCategoryName(productCategory.getCategoryName());
            //设置productInfoVO,商品详情的设置
            List<ProductInfoVO> productInfoVOList = new ArrayList<>();

            for (ProductInfo productInfo: productInfoList) {
                if (productInfo.getCategoryType().equals(productCategory.getCategoryType())) {
                    ProductInfoVO productInfoVO = new ProductInfoVO();
                    //使用BeanUtils,字段相同进行复制
                    BeanUtils.copyProperties(productInfo, productInfoVO);
                    productInfoVOList.add(productInfoVO);
                }
            }
            productVO.setProductInfoVOList(productInfoVOList);
            productVOList.add(productVO);
        }


        return ResultVOUtils.success( productVOList );
    }

}
