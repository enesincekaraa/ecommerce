package com.ecommerce.project.controller;

import com.ecommerce.project.payload.ProductDto;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDto> addProduct(@Valid @RequestBody ProductDto productDto,
                                                 @PathVariable Long categoryId) {
        ProductDto savedProductDto = productService.addProduct(categoryId,productDto);
        return new ResponseEntity<>(savedProductDto,HttpStatus.CREATED);

    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber",defaultValue="0",required = false ) Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue="10",required = false ) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue="productId",required = false ) String sortBy,
            @RequestParam(name = "sortOrder",defaultValue="asc",required = false ) String sortOrder
    ) {
        ProductResponse productResponse = productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long categoryId,
                                                          @RequestParam(name = "pageNumber",defaultValue="0",required = false ) Integer pageNumber,
                                                          @RequestParam(name = "pageSize",defaultValue="10",required = false ) Integer pageSize,
                                                          @RequestParam(name = "sortBy",defaultValue="productId",required = false ) String sortBy,
                                                          @RequestParam(name = "sortOrder",defaultValue="asc",required = false ) String sortOrder) {
        ProductResponse productResponse = productService.searchByCategory(categoryId,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyword(
                                                               @PathVariable String keyword,
                                                               @RequestParam(name = "pageNumber",defaultValue="0",required = false ) Integer pageNumber,
                                                               @RequestParam(name = "pageSize",defaultValue="10",required = false ) Integer pageSize,
                                                               @RequestParam(name = "sortBy",defaultValue="productId",required = false ) String sortBy,
                                                               @RequestParam(name = "sortOrder",defaultValue="asc",required = false ) String sortOrder) {
        ProductResponse productResponse = productService.searchProductByKeyword(keyword,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.FOUND);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@Valid @PathVariable Long productId,
                                                    @RequestBody ProductDto productDto) {
        ProductDto updatedProduct = productService.updateProduct(productId,productDto);
        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDto> deleteProduct(@PathVariable Long productId) {
        ProductDto deletedProduct = productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProduct,HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDto> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image") MultipartFile image) throws IOException {
        ProductDto updatedProduct = productService.updateProductImage(productId,image);
        return new ResponseEntity<>(updatedProduct,HttpStatus.OK);

    }


}
