package com.ecommerce.project.controller;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/categories")
public class CategoryController {

    private final CategoryService categoryService;


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping()
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }


    @PostMapping()
    public String addCategory(@RequestBody Category category) {
        categoryService.createCategory(category);
        return category.toString();
    }



}
