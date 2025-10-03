// CartServiceImpl.java
package com.self.appreciation.cart.service.impl;

import com.self.appreciation.cart.entity.Cart;
import com.self.appreciation.cart.entity.CartItem;
import com.self.appreciation.cart.repository.CartRepository;
import com.self.appreciation.cart.repository.CartItemRepository;
import com.self.appreciation.cart.service.CartService;
import com.self.appreciation.cart.dto.AddToCartRequest;
import com.self.appreciation.cart.dto.CartDTO;
import com.self.appreciation.cart.dto.CartItemDTO;
import com.self.appreciation.cart.api.ProductServiceClient;
import com.self.appreciation.cart.dto.ProductDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;

    public CartServiceImpl(CartRepository cartRepository,
                          CartItemRepository cartItemRepository,
                          ProductServiceClient productServiceClient) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productServiceClient = productServiceClient;
    }

    @Override
    public CartDTO getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setCreateTime(LocalDateTime.now());
                    newCart.setUpdateTime(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });

        CartDTO cartDTO = new CartDTO();
        BeanUtils.copyProperties(cart, cartDTO);

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        List<CartItemDTO> itemDTOs = cartItems.stream().map(item -> {
            CartItemDTO itemDTO = new CartItemDTO();
            BeanUtils.copyProperties(item, itemDTO);
            return itemDTO;
        }).collect(Collectors.toList());

        cartDTO.setItems(itemDTOs);
        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO addToCart(AddToCartRequest request) {
        // 获取或创建购物车
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(request.getUserId());
                    newCart.setCreateTime(LocalDateTime.now());
                    newCart.setUpdateTime(LocalDateTime.now());
                    return cartRepository.save(newCart);
                });

        // 获取商品信息
        ProductDTO product = productServiceClient.getProductById(request.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 检查商品库存
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("商品库存不足");
        }

        // 检查购物车中是否已存在该商品
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCartId(cart.getId());
                    newItem.setProductId(request.getProductId());
                    newItem.setUnitPrice(product.getPrice());
                    newItem.setCreateTime(LocalDateTime.now());
                    newItem.setUpdateTime(LocalDateTime.now());
                    return newItem;
                });

        // 更新数量和时间
        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        cartItem.setUpdateTime(LocalDateTime.now());
        cartItemRepository.save(cartItem);

        // 更新购物车时间
        cart.setUpdateTime(LocalDateTime.now());
        cartRepository.save(cart);

        return getCartByUserId(request.getUserId());
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(Long userId, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("购物车不存在"));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("购物车中不存在该商品"));

        // 获取商品信息检查库存
        ProductDTO product = productServiceClient.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        if (product.getStock() < quantity) {
            throw new RuntimeException("商品库存不足");
        }

        cartItem.setQuantity(quantity);
        cartItem.setUpdateTime(LocalDateTime.now());
        cartItemRepository.save(cartItem);

        cart.setUpdateTime(LocalDateTime.now());
        cartRepository.save(cart);

        return getCartByUserId(userId);
    }

    @Override
    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("购物车不存在"));

        cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId);

        cart.setUpdateTime(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("购物车不存在"));

        cartItemRepository.deleteByCartId(cart.getId());

        cart.setUpdateTime(LocalDateTime.now());
        cartRepository.save(cart);
    }
}
