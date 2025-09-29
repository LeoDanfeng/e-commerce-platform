package com.self.appreciation.order.service;

import com.self.appreciation.order.dto.CreateOrderRequest;
import com.self.appreciation.order.dto.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDTO createOrder(CreateOrderRequest request);
    OrderDTO getOrderById(Long id);
    Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable);
    OrderDTO updateOrderStatus(Long id, String status);
}
