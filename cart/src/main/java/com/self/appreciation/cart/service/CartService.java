// CartService.java
package com.self.appreciation.cart.service;

import com.self.appreciation.cart.dto.AddToCartRequest;
import com.self.appreciation.cart.dto.CartDTO;
import com.self.appreciation.cart.dto.CartItemDTO;

public interface CartService {
    CartDTO getCartByUserId(Long userId);
    CartDTO addToCart(AddToCartRequest request);
    CartDTO updateCartItem(Long userId, Long productId, Integer quantity);
    void removeFromCart(Long userId, Long productId);
    void clearCart(Long userId);
}
