package com.self.appreciation.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;
    private List<OrderItemDTO> items;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
