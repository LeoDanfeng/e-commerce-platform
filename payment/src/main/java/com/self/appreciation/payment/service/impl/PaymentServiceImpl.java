// payment/src/main/java/com/self/appreciation/payment/service/impl/PaymentServiceImpl.java
package com.self.appreciation.payment.service.impl;

import com.self.appreciation.payment.dto.PaymentRequest;
import com.self.appreciation.payment.dto.PaymentResponse;
import com.self.appreciation.payment.entity.Payment;
import com.self.appreciation.payment.repository.PaymentRepository;
import com.self.appreciation.payment.service.PaymentService;
import com.self.appreciation.payment.util.AlipayUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AlipayUtil alipayUtil;

    @Override
    @Transactional
    public Object processPayment(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("PENDING");
        payment.setOutTradeNo(alipayUtil.generateOutTradeNo()); // 生成商户订单号
        payment.setCreateTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());

        // 保存支付记录
        payment = paymentRepository.save(payment);

        // 处理支付宝支付
        if ("ALIPAY".equals(request.getPaymentMethod())) {
            try {
                // 调用支付宝统一下单接口，获取支付表单
                String form = alipayUtil.pagePay(
                        payment.getOutTradeNo(),
                        payment.getAmount(),
                        "订单支付-" + payment.getOrderId()
                );

                if (form != null) return form;

                // 可以将form返回给前端，前端直接提交该表单即可跳转到支付宝支付页面
                log.info("支付宝支付表单生成成功，商户订单号：{}", payment.getOutTradeNo());

                // 这里可以根据实际需求返回支付表单或其他信息
                return convertToDto(payment);
            } catch (Exception e) {
                log.error("调用支付宝接口失败", e);
                payment.setStatus("FAILED");
                paymentRepository.save(payment);
                throw new RuntimeException("支付请求失败");
            }
        } else if ("WECHAT_PAY".equals(request.getPaymentMethod())) {
            // 微信支付逻辑保持不变
            handleWechatPayPayment(payment);
            payment.setStatus("SUCCESS");
            payment.setTransactionId(payment.getOutTradeNo()); // 模拟交易号
            payment.setUpdateTime(LocalDateTime.now());
            payment = paymentRepository.save(payment);
            return convertToDto(payment);
        } else {
            throw new IllegalArgumentException("不支持的支付方式: " + request.getPaymentMethod());
        }
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("支付记录不存在"));
        return convertToDto(payment);
    }

    private void handleWechatPayPayment(Payment payment) {
        // 在实际应用中，这里会调用微信支付SDK进行支付处理
        log.info("处理微信支付，订单ID: {}", payment.getOrderId());
    }

    private PaymentResponse convertToDto(Payment payment) {
        PaymentResponse dto = new PaymentResponse();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrderId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setTransactionId(payment.getTransactionId());
        dto.setCreateTime(payment.getCreateTime());
        dto.setUpdateTime(payment.getUpdateTime());
        return dto;
    }
}
