package com.ecommerce.project.service;

import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDto;
import com.ecommerce.project.payload.ProductResponse;

public interface ProductService {
    ProductDto addProduct(Long categoryId, Product product);

    ProductResponse getAllProducts();

    ProductResponse searchByCategory(Long categoryId);

    ProductResponse searchProductByKeyword(String keyword);
}
