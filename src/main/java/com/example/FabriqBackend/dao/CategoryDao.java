package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryDao extends JpaRepository<Category, Integer> {

    Category findByCategoryCode(String categoryCode);
    Category findById(int id);


    Optional<Category> findByCategoryId(Integer categoryId);


}
