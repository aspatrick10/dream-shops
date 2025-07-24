package com.dailycodework.dreamshops.service.cart;

import java.math.BigDecimal;

import com.dailycodework.dreamshops.model.Cart;

public interface ICartService {
    Cart getCart(Long id);

    void clearCart(Long id);

    BigDecimal getTotalPrice(Long id);

    Long initializeNewCart();

    // TODO: Method to get cart by user ID
}
