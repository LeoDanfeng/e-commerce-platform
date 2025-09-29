package com.self.appreciation.product.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.self.appreciation.product.ProductApplication;
import com.self.appreciation.product.dto.ProductRequest;
import com.self.appreciation.product.entity.Product;
import com.self.appreciation.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ProductApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
//@Transactional
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void testFullProductLifecycle() throws Exception {
        // 1. 创建商品
        ProductRequest request = new ProductRequest();
        request.setName("集成测试商品");
        request.setDescription("集成测试商品描述");
        request.setPrice(new BigDecimal("299.99"));
        request.setStock(50);
        request.setCategory("测试分类");

        // 执行创建请求
        String createResponse = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("集成测试商品"))
                .andExpect(jsonPath("$.price").value(299.99))
                .andExpect(jsonPath("$.stock").value(50))
                .andReturn().getResponse().getContentAsString();

        // 提取创建的商品ID
        Long productId = objectMapper.readTree(createResponse).get("id").asLong();

        // 2. 查询商品
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("集成测试商品"));

        // 3. 更新商品库存
        mockMvc.perform(put("/api/products/{id}/stock/increase?quantity=25", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(75));

        // 4. 验证库存更新
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(75));

        // 5. 减少库存
        mockMvc.perform(put("/api/products/{id}/stock/decrease?quantity=30", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(45));

        // 6. 检查库存是否足够
        mockMvc.perform(get("/api/products/{id}/stock/check?quantity=40", productId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        mockMvc.perform(get("/api/products/{id}/stock/check?quantity=50", productId))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        // 7. 查询所有商品
        mockMvc.perform(get("/api/products?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[*].id", hasItem(productId.intValue())));

        // 8. 按分类查询商品
        mockMvc.perform(get("/api/products/category/测试分类"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name", containsString("集成测试商品")));

        // 9. 删除商品
        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(content().string("商品删除成功"));

        // 10. 验证商品已删除
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testProductValidation() throws Exception {
        // 测试请求验证
        ProductRequest invalidRequest = new ProductRequest();
        invalidRequest.setName(""); // 空名称
        invalidRequest.setPrice(new BigDecimal("-10")); // 负价格
        invalidRequest.setStock(-5); // 负库存

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
