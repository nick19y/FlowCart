package com.flowcart.orderservice.dto;

import com.flowcart.orderservice.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private Long productId;
    private int quantity;
    private double totalPrice;
    private OrderStatus status;
    private LocalDateTime createdAt;
}