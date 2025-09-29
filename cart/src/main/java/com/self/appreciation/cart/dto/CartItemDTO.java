// CartItemDTO.java
package com.self.appreciation.cart.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartItemDTO {
    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
