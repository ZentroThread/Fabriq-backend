package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDao extends TenantAwareDao<Category, Integer> {

    Category findByCategoryCode(String categoryCode);


}
