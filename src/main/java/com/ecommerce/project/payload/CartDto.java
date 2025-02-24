package com.ecommerce.project.payload;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class CartDto {
    private Long id;
    private Long userId;
    private List<CartItemDto> cartItems;
    private BigDecimal totalPrice;
    private Integer totalItems;
}
