// payment/src/main/java/com/self/appreciation/payment/entity/Payment.java
package com.self.appreciation.payment.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "status", nullable = false)
    private String status; // PENDING, SUCCESS, FAILED, REFUNDED

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // ALIPAY, WECHAT_PAY

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "out_trade_no", unique = true)
    private String outTradeNo; // 商户订单号

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
