package com.ecommerce.project.service.impl;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.service.CategoryService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new APIException("No Categories Found.");
        }
        return categories;
    }

    @Override
    public void createCategory(Category category) {

        Category savedCategory=categoryRepository
                .findByCategoryName(category.getCategoryName());
        if(savedCategory!=null){
            throw new APIException("category already exist : " + category.getCategoryName());
        }
        categoryRepository.save(category);

    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category","categoryId",categoryId));
        categoryRepository.delete(category);
        return "Category deleted with id: " + categoryId;
    }

    @Override
    public Category updateCategory(Long categoryId, Category category) {

        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(
                ()-> new ResourceNotFoundException("Category","categoryId",categoryId));


        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);

        return savedCategory;

    }


}
