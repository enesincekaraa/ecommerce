package com.ecommerce.project.controller;

import com.ecommerce.project.payload.CartDto;
import com.ecommerce.project.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartDto> addToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        CartDto cartDto = cartService.addItemToCart(userId, productId, quantity);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }


}
