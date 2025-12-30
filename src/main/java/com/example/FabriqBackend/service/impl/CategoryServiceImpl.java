package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.CategoryDao;
import com.example.FabriqBackend.model.Category;
import com.example.FabriqBackend.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "categories")
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryDao categoryDao;

    @CachePut(key = "#category.tenantId + ':category:' + #category.categoryName")
    public ResponseEntity<?> createCategory(Category category) {

        Category cat = categoryDao.save(category);
        return new ResponseEntity<>(cat, HttpStatus.CREATED);
    }

    @CacheEvict(key = "#id")
    public ResponseEntity<?> deleteCategory(Integer id) {
        categoryDao.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allCategories'")
    public ResponseEntity<?> getAllCategories() {

        return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);
    }
}
