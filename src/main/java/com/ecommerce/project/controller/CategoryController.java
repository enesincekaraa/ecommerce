package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.config.UrlsConstants;
import com.ecommerce.project.payload.CategoryDto;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UrlsConstants.BASE_URL)
public class CategoryController {

    private final CategoryService categoryService;


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping()
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_CATEGORIES_BY,required = false) String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR,required = false) String sortOrder
    ){
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(categoryResponse,HttpStatus.OK);
    }


    @PostMapping()
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto savedCategoryDto = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(savedCategoryDto, HttpStatus.CREATED);
    }

    @DeleteMapping(UrlsConstants.DELETE_CATEGORY)
    public ResponseEntity<CategoryDto> deleteCategory(@PathVariable Long categoryId) {
            CategoryDto categoryDto= categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(categoryDto, HttpStatus.OK);

    }

    @PutMapping(UrlsConstants.UPDATE_CATEGORY)
    public ResponseEntity<CategoryDto> updateCategory(@Valid @PathVariable Long categoryId ,
                                                 @RequestBody CategoryDto categoryDto) {
            CategoryDto savedCategoryDto = categoryService.updateCategory(categoryId,categoryDto);
            return new ResponseEntity<>(savedCategoryDto, HttpStatus.OK);

    }


}
