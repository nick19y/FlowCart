package com.flowcart.orderservice.listener;

import com.flowcart.orderservice.config.RabbitMQConfig;
import com.flowcart.orderservice.event.PaymentProcessedEvent;
import com.flowcart.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResultListener {

    private final OrderService orderService;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_RESULT_QUEUE)
    public void handlePaymentResult(PaymentProcessedEvent event) {
        log.info("Received payment result for order {}: {}", event.getOrderId(), event.getStatus());
        orderService.handlePaymentResult(event);
    }
}