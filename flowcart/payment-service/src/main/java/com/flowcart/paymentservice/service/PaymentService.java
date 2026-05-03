package com.flowcart.paymentservice.service;

import com.flowcart.paymentservice.config.RabbitMQConfig;
import com.flowcart.paymentservice.entity.Payment;
import com.flowcart.paymentservice.entity.PaymentStatus;
import com.flowcart.paymentservice.event.OrderCreatedEvent;
import com.flowcart.paymentservice.event.PaymentProcessedEvent;
import com.flowcart.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public void processPayment(OrderCreatedEvent event) {
        log.info("Processing payment for order {}", event.getOrderId());

        // Simulate payment — always approved
        Payment payment = new Payment();
        payment.setOrderId(event.getOrderId());
        payment.setAmount(event.getTotalPrice());
        payment.setStatus(PaymentStatus.CONFIRMED);

        repository.save(payment);
        log.info("Payment saved for order {} with status CONFIRMED", event.getOrderId());

        // Publish result — both order-service and product-service will consume
        PaymentProcessedEvent result = new PaymentProcessedEvent(
                event.getOrderId(),
                event.getProductId(),
                event.getQuantity(),
                PaymentStatus.CONFIRMED.name()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_CONFIRMED_ROUTING_KEY,
                result
        );

        log.info("PaymentProcessedEvent published for order {}", event.getOrderId());
    }
}