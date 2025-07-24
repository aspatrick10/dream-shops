package com.dailycodework.dreamshops.service.cart;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.model.Cart;
import com.dailycodework.dreamshops.repository.CartItemRepository;
import com.dailycodework.dreamshops.repository.CartRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public Cart getCart(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found with ID: " + id));
    }

    @Override
    public void clearCart(Long id) {
        // Verify cart exists and get it in one call
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found with ID: " + id));

        // Clear all items from the cart (don't delete the cart itself)
        cartItemRepository.deleteAllByCartId(id);

        // Reset total amount to zero
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Override
    public BigDecimal getTotalPrice(Long id) {
        return cartRepository.findTotalAmountById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found with ID: " + id));
    }

    @Override
    public Long initializeNewCart() {
        Cart newCart = new Cart();
        Cart savedCart = cartRepository.save(newCart);
        return savedCart.getId();
    }

    // TODO: Implement getCartByUserId method
}
