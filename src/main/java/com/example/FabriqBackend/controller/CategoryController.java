package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.model.Category;
import com.example.FabriqBackend.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/category")// Base URL for category-related operations
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @PostMapping("/add")
    @Operation(
            summary = "Create a new category",
            description = "Adds a new attire category such as 'Bridal', 'Groom', 'Kids', or any other category. Accepts a Category object and saves it to the database."
    )
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete a category by ID",
            description = "Removes an existing category using its ID. Returns an error response if the category does not exist or is currently in use."
    )
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        return categoryService.deleteCategory(id);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Retrieve all categories",
            description = "Returns a list of all attire categories available in the system."
    )
    public ResponseEntity<?> getAllCategories() {
        return categoryService.getAllCategories();
    }
}
