package com.ecommerce.project.service.impl;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDto;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.payload.GetOneCategoryDto;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }


    @Cacheable(value = "categories",key = "#root.methodName",unless = "#result == null")
    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy,String sortOrder){

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();

        if (categories.isEmpty()) {
            throw new APIException("No Categories Found.");
        }
        List<CategoryDto> categoryDtos=categories
                .stream()
                .map(category -> modelMapper
                        .map(category, CategoryDto.class))
                .toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDtos);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @CachePut(value = "category_name", key = "'getCategoryByName' + #categoryName", unless = "#result == null")
    @Override
    public GetOneCategoryDto getCategoryByName(String categoryName) {
        Category category = categoryRepository.findByCategoryName(categoryName);
        if (category==null){
            throw new ResourceNotFoundException("Category not found","categoryname",categoryName);
        }
        return modelMapper.map(category, GetOneCategoryDto.class);
    }

    @CacheEvict(value = {"categories","category_name"},allEntries = true)
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);
        Category categoryFromDb=categoryRepository
                .findByCategoryName(category.getCategoryName());
        if(categoryFromDb!=null){
            throw new APIException("category already exist : " + category.getCategoryName());
        }
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);

    }

    @CacheEvict(value = {"categories","category_name"},allEntries = true)
    @Override
    public CategoryDto deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new ResourceNotFoundException("Category","categoryId",categoryId));
        categoryRepository.delete(category);
        return modelMapper.map( category, CategoryDto.class);
    }

    @CachePut(value = "category_name"
            ,key = "'getCategoryByName'+#categoryDto.categoryName"
            ,unless = "#result==null")
    @CacheEvict(value = "categories", allEntries = true)
    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(
                        ()->
                                new ResourceNotFoundException("Category","categoryId",categoryId));
        Category mapCategory =modelMapper.map(categoryDto,Category.class);


        mapCategory.setCategoryId(categoryId);
        mapCategory.setCategoryName(categoryDto.getCategoryName());

        Category savedCategory = categoryRepository.save(mapCategory);
        return modelMapper.map(savedCategory,CategoryDto.class);

    }

}
