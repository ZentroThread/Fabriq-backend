package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Category;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryDao extends TenantAwareDao<Category, Integer> {

    Category findByCategoryCode(String categoryCode);
    Category findById(int id);

    Optional<Category> findByCategoryIdAndTenantId(Integer categoryId, String tenantId);
}
