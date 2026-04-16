package com.example.FabriqBackend.service.Interface;

import com.example.FabriqBackend.model.Category;
import org.springframework.http.ResponseEntity;

public interface ICategoryService {
    ResponseEntity<?> createCategory(Category category);
    ResponseEntity<?> deleteCategory(Integer id);
    ResponseEntity<?> getAllCategories();
}
