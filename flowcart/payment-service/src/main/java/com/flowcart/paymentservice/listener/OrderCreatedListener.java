package com.flowcart.paymentservice.listener;

import com.flowcart.paymentservice.config.RabbitMQConfig;
import com.flowcart.paymentservice.event.OrderCreatedEvent;
import com.flowcart.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedListener {

    private final PaymentService paymentService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for order {}", event.getOrderId());
        paymentService.processPayment(event);
    }
}