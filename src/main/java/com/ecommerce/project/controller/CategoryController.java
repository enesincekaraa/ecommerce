package com.ecommerce.project.controller;

import com.ecommerce.project.payload.CategoryDto;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/categories")
public class CategoryController {

    private final CategoryService categoryService;


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping()
    public ResponseEntity<CategoryResponse> getAllCategories() {
        CategoryResponse categoryResponse = categoryService.getAllCategories();
        return new ResponseEntity<>(categoryResponse,HttpStatus.OK);
    }


    @PostMapping()
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto savedCategoryDto = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(savedCategoryDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable Long categoryId) {
            CategoryDto categoryDto= categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(categoryDto, HttpStatus.OK);

    }

    @PutMapping("/{categoryId}/update")
    public ResponseEntity<CategoryDto> updateCategory(@Valid @PathVariable Long categoryId ,
                                                 @RequestBody CategoryDto categoryDto) {
            CategoryDto savedCategoryDto = categoryService.updateCategory(categoryId,categoryDto);
            return new ResponseEntity<>(savedCategoryDto, HttpStatus.OK);

    }


}
