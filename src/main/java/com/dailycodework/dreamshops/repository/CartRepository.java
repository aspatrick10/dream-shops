package com.dailycodework.dreamshops.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dailycodework.dreamshops.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
    // TODO: Implement a method to find a cart by user ID

    Optional<BigDecimal> findTotalAmountById(Long id);
}