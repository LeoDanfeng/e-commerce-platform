package com.self.appreciation.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.self.appreciation.product.dto.ProductDTO;
import com.self.appreciation.product.dto.ProductRequest;
import com.self.appreciation.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void testCreateProduct() throws Exception {
        // 准备测试数据
        ProductRequest request = new ProductRequest();
        request.setName("测试商品");
        request.setDescription("测试商品描述");
        request.setPrice(new BigDecimal("99.99"));
        request.setStock(100);
        request.setCategory("测试分类");

        ProductDTO response = new ProductDTO();
        response.setId(1L);
        response.setName("测试商品");
        response.setDescription("测试商品描述");
        response.setPrice(new BigDecimal("99.99"));
        response.setStock(100);
        response.setCategory("测试分类");

        // 模拟 service 行为
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        // 执行测试
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("测试商品"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    void testGetProduct() throws Exception {
        // 准备测试数据
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("测试商品");
        productDTO.setDescription("测试商品描述");
        productDTO.setPrice(new BigDecimal("99.99"));
        productDTO.setStock(100);
        productDTO.setCategory("测试分类");

        // 模拟 service 行为
        when(productService.getProductById(1L)).thenReturn(productDTO);

        // 执行测试
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("测试商品"));
    }

    @Test
    void testGetProduct_NotFound() throws Exception {
        // 模拟 service 行为
        when(productService.getProductById(1L)).thenThrow(new RuntimeException("商品不存在"));

        // 执行测试
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllProducts() throws Exception {
        // 准备测试数据
        ProductDTO product1 = new ProductDTO();
        product1.setId(1L);
        product1.setName("商品1");
        product1.setPrice(new BigDecimal("100.00"));
        product1.setStock(50);

        ProductDTO product2 = new ProductDTO();
        product2.setId(2L);
        product2.setName("商品2");
        product2.setPrice(new BigDecimal("200.00"));
        product2.setStock(30);

        List<ProductDTO> productList = Arrays.asList(product1, product2);
        Page<ProductDTO> productPage = new PageImpl<>(productList, PageRequest.of(0, 10), 2);

        // 模拟 service 行为
        when(productService.getAllProducts(any())).thenReturn(productPage);

        // 执行测试
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("商品1"));
    }

    @Test
    void testGetProductsByCategory() throws Exception {
        // 准备测试数据
        ProductDTO product1 = new ProductDTO();
        product1.setId(1L);
        product1.setName("商品1");
        product1.setCategory("电子产品");
        product1.setPrice(new BigDecimal("100.00"));
        product1.setStock(50);

        ProductDTO product2 = new ProductDTO();
        product2.setId(2L);
        product2.setName("商品2");
        product2.setCategory("电子产品");
        product2.setPrice(new BigDecimal("200.00"));
        product2.setStock(30);

        List<ProductDTO> productList = Arrays.asList(product1, product2);

        // 模拟 service 行为
        when(productService.getProductsByCategory("电子产品")).thenReturn(productList);

        // 执行测试
        mockMvc.perform(get("/api/products/category/电子产品"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("商品1"))
                .andExpect(jsonPath("$[1].name").value("商品2"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        // 准备测试数据
        ProductRequest request = new ProductRequest();
        request.setName("更新商品");
        request.setDescription("更新商品描述");
        request.setPrice(new BigDecimal("199.99"));
        request.setStock(200);
        request.setCategory("更新分类");

        ProductDTO response = new ProductDTO();
        response.setId(1L);
        response.setName("更新商品");
        response.setDescription("更新商品描述");
        response.setPrice(new BigDecimal("199.99"));
        response.setStock(200);
        response.setCategory("更新分类");

        // 模拟 service 行为
        when(productService.updateProduct(eq(1L), any(ProductRequest.class))).thenReturn(response);

        // 执行测试
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("更新商品"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        // 执行测试
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("商品删除成功"));
    }

    @Test
    void testDecreaseStock() throws Exception {
        // 准备测试数据
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("测试商品");
        productDTO.setStock(70);

        // 模拟 service 行为
        when(productService.decreaseStock(1L, 30)).thenReturn(true);
        when(productService.getProductById(1L)).thenReturn(productDTO);

        // 执行测试
        mockMvc.perform(put("/api/products/1/stock/decrease?quantity=30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.stock").value(70));
    }

    @Test
    void testIncreaseStock() throws Exception {
        // 准备测试数据
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("测试商品");
        productDTO.setStock(150);

        // 模拟 service 行为
        when(productService.increaseStock(1L, 50)).thenReturn(true);
        when(productService.getProductById(1L)).thenReturn(productDTO);

        // 执行测试
        mockMvc.perform(put("/api/products/1/stock/increase?quantity=50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.stock").value(150));
    }

    @Test
    void testCheckStock() throws Exception {
        // 模拟 service 行为
        when(productService.checkStock(1L, 50)).thenReturn(true);

        // 执行测试
        mockMvc.perform(get("/api/products/1/stock/check?quantity=50"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testUpdateStock() throws Exception {
        // 准备测试数据
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("测试商品");
        productDTO.setStock(200);

        // 模拟 service 行为
        when(productService.updateStock(1L, 200)).thenReturn(productDTO);

        // 执行测试
        mockMvc.perform(put("/api/products/1/stock?stock=200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.stock").value(200));
    }
}
