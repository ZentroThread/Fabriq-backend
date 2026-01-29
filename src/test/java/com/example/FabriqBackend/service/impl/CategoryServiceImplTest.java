package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.CategoryDao;
import com.example.FabriqBackend.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryDao categoryDao;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryId(1);
        category.setCategoryName("Shirts");
    }

    // ---------- CREATE ----------

    @Test
    void shouldCreateCategorySuccessfully() {
        when(categoryDao.save(category)).thenReturn(category);

        ResponseEntity<?> response = categoryService.createCategory(category);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(category, response.getBody());
        verify(categoryDao).save(category);
    }

    // ---------- DELETE ----------

    @Test
    void shouldDeleteCategorySuccessfully() {
        doNothing().when(categoryDao).deleteById(1);

        ResponseEntity<?> response = categoryService.deleteCategory(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(categoryDao).deleteById(1);
    }

    // ---------- READ ----------

    @Test
    void shouldReturnAllCategories() {
        when(categoryDao.findAll()).thenReturn(List.of(category));

        ResponseEntity<?> response = categoryService.getAllCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(categoryDao).findAll();
    }
}
