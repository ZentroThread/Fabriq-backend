package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dao.CategoryDao;
import com.example.FabriqBackend.model.Category;
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
public class CategoryService {

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

    @Cacheable(key = "'allCategories'")
    public ResponseEntity<?> getAllCategories() {

        return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);
    }
}
