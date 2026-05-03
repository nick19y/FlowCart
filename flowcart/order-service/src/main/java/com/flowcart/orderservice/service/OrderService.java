package com.flowcart.orderservice.service;

import com.flowcart.orderservice.client.ProductClient;
import com.flowcart.orderservice.config.RabbitMQConfig;
import com.flowcart.orderservice.dto.OrderRequest;
import com.flowcart.orderservice.dto.OrderResponse;
import com.flowcart.orderservice.dto.ProductResponse;
import com.flowcart.orderservice.entity.Order;
import com.flowcart.orderservice.entity.OrderStatus;
import com.flowcart.orderservice.event.OrderCreatedEvent;
import com.flowcart.orderservice.event.PaymentProcessedEvent;
import com.flowcart.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final ProductClient productClient;
    private final RabbitTemplate rabbitTemplate;

    public OrderResponse create(OrderRequest request) {
        // 1. Fetch product via Feign
        ProductResponse product = productClient.getProductById(request.getProductId());

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        // 2. Calculate total price
        double totalPrice = product.getPrice() * request.getQuantity();

        // 3. Save order with PENDING status
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(totalPrice);

        Order saved = repository.save(order);
        log.info("Order {} created with status PENDING", saved.getId());

        // 4. Publish OrderCreatedEvent to RabbitMQ
        OrderCreatedEvent event = new OrderCreatedEvent(
                saved.getId(),
                saved.getProductId(),
                saved.getQuantity(),
                saved.getTotalPrice()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
                event
        );

        log.info("OrderCreatedEvent published for order {}", saved.getId());
        return toResponse(saved);
    }

    public List<OrderResponse> getAll() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse getById(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return toResponse(order);
    }

    public void handlePaymentResult(PaymentProcessedEvent event) {
        Order order = repository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus newStatus = event.getStatus().equals("CONFIRMED")
                ? OrderStatus.CONFIRMED
                : OrderStatus.FAILED;

        order.setStatus(newStatus);
        repository.save(order);
        log.info("Order {} updated to status {}", order.getId(), newStatus);
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getProductId(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    public List<OrderResponse> getByUserId(Long userId) {
    return repository.findByUserId(userId).stream()
        .map(this::toResponse)
        .toList();
    }
}