package com.dailycodework.dreamshops.service.cart;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.dailycodework.dreamshops.dto.CartItemDto;
import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.model.Cart;
import com.dailycodework.dreamshops.model.CartItem;
import com.dailycodework.dreamshops.model.Product;
import com.dailycodework.dreamshops.repository.CartItemRepository;
import com.dailycodework.dreamshops.repository.CartRepository;
import com.dailycodework.dreamshops.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ICartService cartService;
    private final ModelMapper modelMapper;

    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        // If cartId is null, initialize a new cart
        cartId = Optional.ofNullable(cartId)
                .orElseGet(() -> cartService.initializeNewCart());

        // 1. Get the cart
        Cart cart = cartService.getCart(cartId);

        // 2. Get the product
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product not found"));

        // 3. Check if item already exists in cart
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElse(new CartItem());

        // 4. If item exists, update quantity; otherwise create new item
        if (cartItem.getId() != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());

        }
        // Ensure total price is set
        cartItem.setTotalPrice();
        // Update the cart's total amount
        cart.addItem(cartItem);
        // Save the cart item
        cartItemRepository.save(cartItem);
        // Save the cart to persist the updated total amount
        cartRepository.save(cart);
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        CartItem itemToRemove = getCartItem(cartId, productId);
        cart.removeItem(itemToRemove); // This properly updates the cart's total
                                       // amount
        cartItemRepository.delete(itemToRemove);
        cartRepository.save(cart); // Save the cart to persist the updated total
                                   // amount
    }

    @Override
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCart(cartId);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(
                        () -> new ResourceNotFoundException("Item not found"));

        // Remove the item from cart (this triggers updateTotalAmount)
        cart.removeItem(cartItem);

        // Update the quantity and add it back (this triggers updateTotalAmount
        // again)
        cartItem.setQuantity(quantity);
        cartItem.setUnitPrice(cartItem.getProduct().getPrice());
        cartItem.setTotalPrice();
        cart.addItem(cartItem);

        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        return cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(
                        () -> new ResourceNotFoundException("Item not found"));
    }

    // Utility DTO conversion methods
    @Override
    public CartItemDto toCartItemDto(CartItem cartItem) {
        return modelMapper.map(cartItem, CartItemDto.class);
    }
}
