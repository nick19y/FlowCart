package com.flowcart.paymentservice.controller;

import com.flowcart.paymentservice.entity.Payment;
import com.flowcart.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository repository;

    @GetMapping
    public ResponseEntity<List<Payment>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                repository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Payment not found"))
        );
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(
            repository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"))
        );
    }
}