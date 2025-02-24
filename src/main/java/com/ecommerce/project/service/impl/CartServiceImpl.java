package com.ecommerce.project.service.impl;

import com.ecommerce.project.exceptions.InsufficientStockException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.CartDto;
import com.ecommerce.project.repository.CartItemRepository;
import com.ecommerce.project.repository.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartDto addItemToCart(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Tüm sepetlerdeki bu ürünün toplam miktarını hesapla
        int totalQuantityInCarts = cartRepository.findAll().stream()
                .flatMap(c -> c.getCartItems().stream())
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .mapToInt(CartItem::getQuantity)
                .sum();

        // Mevcut kullanıcının sepetindeki miktarı çıkar (güncelleme durumu için)
        Cart existingCart = cartRepository.findByUserId(userId).orElse(null);
        int currentUserQuantity = 0;
        if (existingCart != null) {
            currentUserQuantity = existingCart.getCartItems().stream()
                    .filter(item -> item.getProduct().getProductId().equals(productId))
                    .mapToInt(CartItem::getQuantity)
                    .sum();
            totalQuantityInCarts -= currentUserQuantity;
        }

        // Yeni istenen miktar ile toplam kontrol
        if (product.getQuantity() <= 0 || product.getQuantity() < quantity) {
            throw new InsufficientStockException(
                    String.format("Insufficient stock. Available: %d, Requested: %d",
                            product.getQuantity(), quantity));
        }

        Cart cart = cartRepository.findByUserId(userId).orElse(new Cart());

        if (cart.getId() == null) {
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }

        Optional<CartItem> existingItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst();

        // Ürün stoğunu güncelle
        int quantityDifference = quantity - currentUserQuantity;
        product.setQuantity(product.getQuantity() - quantityDifference);
        productRepository.save(product);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(quantity);
            item.setPrice(BigDecimal.valueOf(product.getPrice()));
            item.setTotalPrice(item.getPrice().multiply(BigDecimal.valueOf(quantity)));
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPrice(BigDecimal.valueOf(product.getPrice()));
            newItem.setTotalPrice(newItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
            cart.getCartItems().add(newItem);
        }

        updateCartTotals(cart);
        cart = cartRepository.save(cart);
        return modelMapper.map(cart, CartDto.class);
    }

    @Override
    public CartDto getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(
                ()-> new ResourceNotFoundException("Cart not found for user","userId: " ,userId));
        return modelMapper.map(cart,CartDto.class);

    }



    private void updateCartTotals(Cart cart) {
        BigDecimal totalPrice = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalItems = cart.getCartItems().stream()
                .map(CartItem::getQuantity)
                .reduce(0, Integer::sum);

        cart.setTotalPrice(totalPrice);
        cart.setTotalItems(totalItems);
    }
}
