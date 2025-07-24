package com.dailycodework.dreamshops.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dailycodework.dreamshops.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    void deleteAllByCartId(Long cartId);
}
