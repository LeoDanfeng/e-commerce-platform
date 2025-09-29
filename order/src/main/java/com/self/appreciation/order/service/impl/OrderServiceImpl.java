// OrderServiceImpl.java
package com.self.appreciation.order.service.impl;

import com.self.appreciation.order.entity.Order;
import com.self.appreciation.order.entity.OrderItem;
import com.self.appreciation.order.repository.OrderRepository;
import com.self.appreciation.order.service.OrderService;
import com.self.appreciation.order.dto.CreateOrderRequest;
import com.self.appreciation.order.dto.OrderDTO;
import com.self.appreciation.order.dto.OrderItemDTO;
import com.self.appreciation.order.dto.OrderItemRequest;
import com.self.appreciation.order.client.ProductServiceClient;
import com.self.appreciation.order.dto.ProductDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    public OrderServiceImpl(OrderRepository orderRepository, ProductServiceClient productServiceClient) {
        this.orderRepository = orderRepository;
        this.productServiceClient = productServiceClient;
    }

    @Override
    @Transactional
    public OrderDTO createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus("CREATED");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        List<OrderItem> orderItems = request.getItems().stream().map(itemRequest -> {
            ProductDTO product = productServiceClient.getProductById(itemRequest.getProductId());

            if (product == null) {
                throw new RuntimeException("商品不存在");
            }

            OrderItem item = new OrderItem();
            item.setProductId(itemRequest.getProductId());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.setOrder(order); // 建立与Order的关联
            return item;
        }).collect(Collectors.toList());

        // 计算总金额
        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalAmount);
        order.setItems(orderItems); // 设置订单项集合

        Order savedOrder = orderRepository.save(order);

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(savedOrder, orderDTO);

        List<OrderItemDTO> itemDTOs = savedOrder.getItems().stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            BeanUtils.copyProperties(item, itemDTO);
            return itemDTO;
        }).collect(Collectors.toList());

        orderDTO.setItems(itemDTOs);
        return orderDTO;
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(order, orderDTO);

        List<OrderItemDTO> itemDTOs = order.getItems().stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            BeanUtils.copyProperties(item, itemDTO);
            return itemDTO;
        }).collect(Collectors.toList());

        orderDTO.setItems(itemDTOs);
        return orderDTO;
    }

    @Override
    public Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);

        List<OrderDTO> orderDTOs = orderPage.getContent().stream().map(order -> {
            OrderDTO orderDTO = new OrderDTO();
            BeanUtils.copyProperties(order, orderDTO);

            List<OrderItemDTO> itemDTOs = order.getItems().stream().map(item -> {
                OrderItemDTO itemDTO = new OrderItemDTO();
                BeanUtils.copyProperties(item, itemDTO);
                return itemDTO;
            }).collect(Collectors.toList());

            orderDTO.setItems(itemDTOs);
            return orderDTO;
        }).collect(Collectors.toList());

        return new PageImpl<>(orderDTOs, pageable, orderPage.getTotalElements());
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);

        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(updatedOrder, orderDTO);

        List<OrderItemDTO> itemDTOs = updatedOrder.getItems().stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            BeanUtils.copyProperties(item, itemDTO);
            return itemDTO;
        }).collect(Collectors.toList());

        orderDTO.setItems(itemDTOs);
        return orderDTO;
    }
}
