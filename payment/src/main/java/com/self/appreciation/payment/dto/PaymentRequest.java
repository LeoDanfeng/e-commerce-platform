package com.self.appreciation.payment.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "支付金额不能为空")
    @Positive(message = "支付金额必须大于0")
    private BigDecimal amount;

    @NotNull(message = "支付方式不能为空")
    private String paymentMethod; // ALIPAY, WECHAT_PAY
}
