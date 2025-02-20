package com.ecommerce.project.service.impl;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDto;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.service.FileService;
import com.ecommerce.project.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;

    @Value("${project.image}")
    private String path;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper, FileService fileService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.fileService = fileService;
    }

    @CacheEvict(value = {"products","search_keyword","search_category"},allEntries = true)
    @Override
    public ProductDto addProduct(Long categoryId, ProductDto productDto) {
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Category", "categoryId", categoryId));

        boolean ifProductNotPresent=true;

        List<Product> products = category.getProducts();
        for (int i = 0; i<products.size();i++){
            if (products.get(i).getProductName().equals(productDto.getProductName())){
                ifProductNotPresent=false;
                break;
            }
        }
        if (ifProductNotPresent){
        Product product = modelMapper.map(productDto, Product.class);

        product.setImage("default.png");
        product.setCategory(category);
        double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
        product.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDto.class);
        }else {
            throw new APIException("Product already exists");
        }
    }


    @Cacheable(value = "products",key = "#root.methodName",unless = "#result == null")
    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);

        List<Product> products = productPage.getContent();

        if (products.isEmpty()){
            throw new APIException("No products found");
        }else {

            List<ProductDto> productDtos = products.stream().map(product ->
                            modelMapper.map(product, ProductDto.class))
                    .collect(Collectors.toList());

            ProductResponse productResponse = new ProductResponse();
            productResponse.setContent(productDtos);
            productResponse.setPageNumber(productPage.getNumber());
            productResponse.setPageSize(productPage.getSize());
            productResponse.setTotalPages(productPage.getTotalPages());
            productResponse.setTotalElements(productPage.getTotalElements());
            productResponse.setLastPage(productPage.isLast());
            return productResponse;
        }

    }

    @CachePut(value = "search_category",key = "'searchByCategory' + #categoryId",unless = "#result==null")
    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Category", "categoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);

        List<Product> products = productPage.getContent();

        if (products.isEmpty()){
            throw new APIException("No products found by this categoryname: " + category.getCategoryName());
        }else {
            List<ProductDto> productDtos = products.stream().map(product ->
                            modelMapper.map(product, ProductDto.class)).collect(Collectors.toList());

            ProductResponse productResponse = new ProductResponse();
            productResponse.setContent(productDtos);
            productResponse.setPageNumber(productPage.getNumber());
            productResponse.setPageSize(productPage.getSize());
            productResponse.setTotalPages(productPage.getTotalPages());
            productResponse.setTotalElements(productPage.getTotalElements());
            productResponse.setLastPage(productPage.isLast());
            return productResponse;
        }

    }

    @Override
    @CachePut(value = "search_keyword",key = "'searchProductByKeyword'+#keyword",unless = "#result==0")
    public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword +'%' ,pageDetails);



            List<Product> products = productPage.getContent();
            if (products.size()==0){
                throw new APIException("Product not found with keyword: " + keyword);
            }
            List<ProductDto> productDtos = products.stream()
                    .map(x -> modelMapper.map(x, ProductDto.class))
                    .collect(Collectors.toList());

            if (products.size()==0){
                throw new APIException("Product not found with keyword: " + keyword);
            }
            ProductResponse productResponse = new ProductResponse();
            productResponse.setContent(productDtos);
            productResponse.setPageNumber(productPage.getNumber());
            productResponse.setPageSize(productPage.getSize());
            productResponse.setTotalPages(productPage.getTotalPages());
            productResponse.setTotalElements(productPage.getTotalElements());
            productResponse.setLastPage(productPage.isLast());
            return productResponse;

    }



    @CachePut(value = "products", key = "#productId", unless = "#result == null")
    @Override
    public ProductDto updateProduct(Long productId, ProductDto productDto) {
        Product productFromDb= productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product", "productId", productId));

        Product product = modelMapper.map(productDto, Product.class);

        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setDiscount(product.getDiscount());
        double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
        productFromDb.setSpecialPrice(specialPrice);

        productRepository.save(productFromDb);
        return modelMapper.map(productFromDb, ProductDto.class);
    }

    @CacheEvict(value = {"products","search_keyword","search_category"},allEntries = true)
    @Override
    public ProductDto deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product", "productId", productId));
        productRepository.delete(product);
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public ProductDto updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product = productRepository.findById(productId).orElseThrow(
                ()->new ResourceNotFoundException("Product", "productId", productId)
        );

        String fileName = fileService.uploadImage(path,image);
        product.setImage(fileName);
        Product updatedProduct = productRepository.save(product);
        return modelMapper.map(updatedProduct, ProductDto.class);
    }

}
