package com.dongtian.epos.service.impl;

import com.dongtian.epos.entity.ProductCategory;
import com.dongtian.epos.repository.ProductCategoryRepository;
import com.dongtian.epos.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private ProductCategoryRepository categoryRepository;
    @Override
    public ProductCategory getOne(Integer id) {
        return categoryRepository.getOne( id );
    }

    @Override
    public List<ProductCategory> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<ProductCategory> findByCategoryTypeIn(List<Integer> categoryTypeList) {
        return categoryRepository.findByCategoryTypeIn( categoryTypeList );
    }

    @Override
    public ProductCategory save(ProductCategory productCategory) {
        return categoryRepository.save( productCategory );
    }
}
