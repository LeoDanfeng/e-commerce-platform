package com.self.appreciation.product.service;

import com.self.appreciation.product.entity.Product;
import com.self.appreciation.product.dto.ProductDTO;
import com.self.appreciation.product.dto.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductRequest productRequest);
    ProductDTO getProductById(Long id);
    Page<ProductDTO> getAllProducts(Pageable pageable);
    List<ProductDTO> getProductsByCategory(String category);
    ProductDTO updateProduct(Long id, ProductRequest productRequest);
    void deleteProduct(Long id);

    // 库存管理相关方法
    boolean decreaseStock(Long productId, Integer quantity);
    boolean increaseStock(Long productId, Integer quantity);
    boolean checkStock(Long productId, Integer quantity);
    ProductDTO updateStock(Long productId, Integer stock);
}
