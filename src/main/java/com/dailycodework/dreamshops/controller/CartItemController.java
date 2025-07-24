package com.dailycodework.dreamshops.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dailycodework.dreamshops.dto.CartItemDto;
import com.dailycodework.dreamshops.model.CartItem;
import com.dailycodework.dreamshops.request.AddCartItemRequest;
import com.dailycodework.dreamshops.request.UpdateCartItemRequest;
import com.dailycodework.dreamshops.response.ApiResponse;
import com.dailycodework.dreamshops.service.cart.ICartItemService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cartItems")
public class CartItemController {
    private final ICartItemService cartItemService;

    @PostMapping("/item/add")
    public ResponseEntity<ApiResponse> addItemToCart(
            @Valid @RequestBody AddCartItemRequest request) {
        cartItemService.addItemToCart(request.getCartId(),
                request.getProductId(), request.getQuantity());
        return ResponseEntity
                .ok(new ApiResponse("Item added to cart successfully", null));
    }

    @DeleteMapping("/cart/{cartId}/product/{productId}/remove")
    public ResponseEntity<ApiResponse> removeItemFromCart(
            @PathVariable Long cartId, @PathVariable Long productId) {
        cartItemService.removeItemFromCart(cartId, productId);
        return ResponseEntity.ok(
                new ApiResponse("Item removed from cart successfully", null));
    }

    @PutMapping("/cart/{cartId}/product/{productId}/update")
    public ResponseEntity<ApiResponse> updateItemQuantity(
            @PathVariable Long cartId, @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        cartItemService.updateItemQuantity(cartId, productId,
                request.getQuantity());
        return ResponseEntity.ok(
                new ApiResponse("Item quantity updated successfully", null));
    }

    @GetMapping("/cart/{cartId}/product/{productId}")
    public ResponseEntity<ApiResponse> getCartItem(@PathVariable Long cartId,
            @PathVariable Long productId) {
        CartItem cartItem = cartItemService.getCartItem(cartId, productId);
        CartItemDto cartItemDto = cartItemService.toCartItemDto(cartItem);
        return ResponseEntity.ok(new ApiResponse("Item found", cartItemDto));
    }
}
