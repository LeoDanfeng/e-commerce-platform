// payment/src/main/java/com/self/appreciation/payment/repository/PaymentRepository.java
package com.self.appreciation.payment.repository;

import com.self.appreciation.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOutTradeNo(String outTradeNo);

    Payment findByOrderId(Long orderId);
}
