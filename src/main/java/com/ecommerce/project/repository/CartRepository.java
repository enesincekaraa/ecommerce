package com.ecommerce.project.repository;

import com.ecommerce.project.model.Cart;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c WHERE c.user.userId = :userId")
    Optional<Cart> findByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) > 0 FROM Cart c WHERE c.user.userId = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}