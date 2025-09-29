package com.self.appreciation.payment.service;

import com.self.appreciation.payment.dto.PaymentRequest;
import com.self.appreciation.payment.dto.PaymentResponse;

public interface PaymentService {
    Object processPayment(PaymentRequest request);
    PaymentResponse getPaymentById(Long id);
}
