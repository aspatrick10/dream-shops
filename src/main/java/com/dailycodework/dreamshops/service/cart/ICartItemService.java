package com.dailycodework.dreamshops.service.cart;

import com.dailycodework.dreamshops.dto.CartItemDto;
import com.dailycodework.dreamshops.model.CartItem;

public interface ICartItemService {
    void addItemToCart(Long cartId, Long productId, int quantity);

    void removeItemFromCart(Long cartId, Long productId);

    void updateItemQuantity(Long cartId, Long productId, int quantity);

    CartItem getCartItem(Long cartId, Long productId);

    // Utility DTO conversion methods
    CartItemDto toCartItemDto(CartItem cartItem);
}
