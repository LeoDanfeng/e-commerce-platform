package com.self.appreciation.product.controller;

import com.self.appreciation.product.service.ProductService;
import com.self.appreciation.product.dto.ProductDTO;
import com.self.appreciation.product.dto.ProductRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        ProductDTO product = productService.createProduct(productRequest);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable("id") Long id) {
        try {
            ProductDTO product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> productPage = productService.getAllProducts(pageable);
        return ResponseEntity.ok(productPage);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable("category") String category) {
        List<ProductDTO> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id") Long id,
                                                    @Valid @RequestBody ProductRequest productRequest) {
        try {
            ProductDTO updatedProduct = productService.updateProduct(id, productRequest);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("商品删除成功");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 库存管理相关接口

    @PutMapping("/{id}/stock/decrease")
    public ResponseEntity<ProductDTO> decreaseStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity) {
        try {
            productService.decreaseStock(id, quantity);
            ProductDTO updatedProduct = productService.getProductById(id);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/stock/increase")
    public ResponseEntity<ProductDTO> increaseStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity) {
        try {
            productService.increaseStock(id, quantity);
            ProductDTO updatedProduct = productService.getProductById(id);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/stock/check")
    public ResponseEntity<Boolean> checkStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity) {
        try {
            boolean hasStock = productService.checkStock(id, quantity);
            return ResponseEntity.ok(hasStock);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductDTO> updateStock(@PathVariable("id") Long id, @RequestParam("stock") Integer stock) {
        try {
            ProductDTO updatedProduct = productService.updateStock(id, stock);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
