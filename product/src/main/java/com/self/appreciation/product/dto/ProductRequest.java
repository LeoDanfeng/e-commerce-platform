package com.self.appreciation.product.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "商品名称不能为空")
    private String name;

    private String description;

    @NotNull(message = "价格不能为空")
    @PositiveOrZero(message = "价格必须大于等于0")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    @PositiveOrZero(message = "库存必须大于等于0")
    private Integer stock = 0;

    private String category;
}
