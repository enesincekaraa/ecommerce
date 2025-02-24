package com.ecommerce.project.service;

import com.ecommerce.project.payload.CartDto;

public interface CartService {
    CartDto addItemToCart(Long userId,Long productId,Integer quantity);
    CartDto getCart(Long userId);
}
