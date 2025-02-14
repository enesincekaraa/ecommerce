package com.ecommerce.project.service;

import com.ecommerce.project.payload.ProductDto;
import com.ecommerce.project.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDto addProduct(Long categoryId, ProductDto productDto);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse searchByCategory(Long categoryId);

    ProductResponse searchProductByKeyword(String keyword);

    ProductDto updateProduct(Long productId, ProductDto productDto);

    ProductDto deleteProduct(Long productId);

    ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException;
}
