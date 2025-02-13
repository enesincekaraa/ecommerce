package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDto;
import com.ecommerce.project.payload.CategoryResponse;


public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDto createCategory(CategoryDto categoryDto);
    CategoryDto deleteCategory(Long categoryId);
    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);
}
