// CartItemRepository.java
package com.self.appreciation.cart.repository;

import com.self.appreciation.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    void deleteByCartIdAndProductId(Long cartId, Long productId);
    void deleteByCartId(Long cartId);
}
