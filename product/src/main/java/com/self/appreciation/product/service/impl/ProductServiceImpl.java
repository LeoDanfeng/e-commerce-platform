package com.self.appreciation.product.service.impl;

import com.self.appreciation.product.entity.Product;
import com.self.appreciation.product.repository.ProductRepository;
import com.self.appreciation.product.service.ProductService;
import com.self.appreciation.product.dto.ProductDTO;
import com.self.appreciation.product.dto.ProductRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductDTO createProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        product.setCategory(productRequest.getCategory());

        Product savedProduct = productRepository.save(product);

        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(savedProduct, productDTO);
        return productDTO;
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在，ID: " + id));

        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(product, productDTO);
        return productDTO;
    }

    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductDTO> productDTOList = productPage.getContent().stream()
                .map(product -> {
                    ProductDTO productDTO = new ProductDTO();
                    BeanUtils.copyProperties(product, productDTO);
                    return productDTO;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOList, pageable, productPage.getTotalElements());
    }

    @Override
    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(product -> {
                    ProductDTO productDTO = new ProductDTO();
                    BeanUtils.copyProperties(product, productDTO);
                    return productDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在，ID: " + id));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        product.setCategory(productRequest.getCategory());

        Product updatedProduct = productRepository.save(product);

        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(updatedProduct, productDTO);
        return productDTO;
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("商品不存在，ID: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean decreaseStock(Long productId, Integer quantity) {
        int updatedRows = productRepository.decreaseStock(productId, quantity);
        if (updatedRows == 0) {
            // 没有更新任何行，说明库存不足或商品不存在
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                throw new RuntimeException("商品不存在，ID: " + productId);
            }
            throw new RuntimeException("库存不足，当前库存: " + product.getStock() + ", 需要: " + quantity);
        }

        /*
          为了进行MOCK测试，先查询再判断数量，实际应该用上面的方法直接update原子更新数量，防止线程并发问题
        */
//        Product product;
//        try {
//            product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("商品不存在，ID: " + productId));
//            if (product.getStock() < quantity) {
//                throw new RuntimeException("库存不足，当前库存: " + product.getStock() + ", 需要: " + quantity);
//            }
//        } catch (RuntimeException e) {
//            return false;
//        }
//        product.setStock(product.getStock() - quantity);
//        productRepository.save(product);
        return true;
    }

    @Override
    @Transactional
    public boolean increaseStock(Long productId, Integer quantity) {
        int updatedRows = productRepository.increaseStock(productId, quantity);
        if (updatedRows == 0) {
            throw new RuntimeException("商品不存在，ID: " + productId);
        }
        return true;
    }

    @Override
    public boolean checkStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在，ID: " + productId));

        return product.getStock() >= quantity;
    }

    @Override
    @Transactional
    public ProductDTO updateStock(Long productId, Integer stock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在，ID: " + productId));

        product.setStock(stock);
        Product updatedProduct = productRepository.save(product);

        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(updatedProduct, productDTO);
        return productDTO;
    }
}
