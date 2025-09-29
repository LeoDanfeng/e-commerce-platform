// CartDTO.java
package com.self.appreciation.cart.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private Long userId;
    private List<CartItemDTO> items;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
