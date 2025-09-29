package com.self.appreciation.product.repository;

import com.self.appreciation.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String name);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET stock = stock - :quantity WHERE id = :productId AND stock >= :quantity", nativeQuery = true)
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET stock = stock + :quantity WHERE id = :productId", nativeQuery = true)
    int increaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
