package com.ecommerce.project.service.impl;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDto;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDto addProduct(Long categoryId, Product product) {
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Category", "categoryId", categoryId));
        product.setImage("default.png");
        product.setCategory(category);
        double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
        product.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public ProductResponse getAllProducts() {
       List<Product> products = productRepository.findAll();
       List<ProductDto> productDtos = products.stream().map(product ->
               modelMapper.map(product, ProductDto.class))
               .collect(Collectors.toList());
       ProductResponse productResponse = new ProductResponse();
       productResponse.setContent(productDtos);
       return productResponse;

    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Category", "categoryId", categoryId));

        List<Product> products = productRepository
                .findByCategoryOrderByPriceAsc(category);
        List<ProductDto> productDtos = products.stream().map(product ->
                        modelMapper.map(product, ProductDto.class))
                .collect(Collectors.toList());
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDtos);
        return productResponse;


    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword) {
        return null;
    }
}
