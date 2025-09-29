package com.self.appreciation.product.repository;

import com.self.appreciation.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testSaveAndFindProduct() {
        // 创建测试商品
        Product product = new Product();
        product.setName("测试商品");
        product.setDescription("测试商品描述");
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(100);
        product.setCategory("测试分类");

        // 保存商品
        Product savedProduct = productRepository.save(product);

        // 验证保存成功
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("测试商品");

        // 根据ID查找商品
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("测试商品");
    }

    @Test
    void testFindByCategory() {
        // 创建测试商品
        Product product1 = new Product();
        product1.setName("商品1");
        product1.setCategory("电子产品");
        product1.setPrice(new BigDecimal("100.00"));
        product1.setStock(50);

        Product product2 = new Product();
        product2.setName("商品2");
        product2.setCategory("电子产品");
        product2.setPrice(new BigDecimal("200.00"));
        product2.setStock(30);

        Product product3 = new Product();
        product3.setName("商品3");
        product3.setCategory("服装");
        product3.setPrice(new BigDecimal("50.00"));
        product3.setStock(100);

        // 保存商品
        entityManager.persistAndFlush(product1);
        entityManager.persistAndFlush(product2);
        entityManager.persistAndFlush(product3);

        // 根据分类查找商品
        List<Product> electronicsProducts = productRepository.findByCategory("电子产品");
        assertThat(electronicsProducts).hasSize(2);
        assertThat(electronicsProducts).extracting(Product::getName)
                .containsExactlyInAnyOrder("商品1", "商品2");
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        // 创建测试商品
        Product product1 = new Product();
        product1.setName("iPhone 15");
        product1.setCategory("手机");
        product1.setPrice(new BigDecimal("7999.00"));
        product1.setStock(50);

        Product product2 = new Product();
        product2.setName("Samsung Galaxy");
        product2.setCategory("手机");
        product2.setPrice(new BigDecimal("5999.00"));
        product2.setStock(30);

        // 保存商品
        entityManager.persistAndFlush(product1);
        entityManager.persistAndFlush(product2);

        // 根据名称模糊查找商品
        List<Product> iphoneProducts = productRepository.findByNameContainingIgnoreCase("iphone");
        assertThat(iphoneProducts).hasSize(1);
        assertThat(iphoneProducts.get(0).getName()).isEqualTo("iPhone 15");

        List<Product> phoneProducts = productRepository.findByNameContainingIgnoreCase("phone");
        assertThat(phoneProducts).hasSize(1);
        assertThat(phoneProducts.get(0).getName()).isEqualTo("iPhone 15");
    }
}
