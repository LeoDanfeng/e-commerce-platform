package com.self.appreciation.order.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class OrderItemRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "数量不能为空")
    @Positive(message = "数量必须大于0")
    private Integer quantity;
}
