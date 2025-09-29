package com.self.appreciation.product.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ProductTest {

    @Test
    void testProductCreation() {
        Product product = new Product();
        product.setName("测试商品");
        product.setDescription("测试商品描述");
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(100);
        product.setCategory("测试分类");

        assertEquals("测试商品", product.getName());
        assertEquals("测试商品描述", product.getDescription());
        assertEquals(new BigDecimal("99.99"), product.getPrice());
        assertEquals(100, product.getStock());
        assertEquals("测试分类", product.getCategory());
    }

    @Test
    void testProductDefaultValues() {
        Product product = new Product();

//        assertNotNull(product.getCreateTime());
//        assertNotNull(product.getUpdateTime());
        assertEquals(0, product.getStock());
        assertNull(product.getName());
        assertNull(product.getDescription());
        assertNull(product.getPrice());
        assertNull(product.getCategory());
    }
}
