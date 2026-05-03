package com.flowcart.productservice.listener;

import com.flowcart.productservice.config.RabbitMQConfig;
import com.flowcart.productservice.event.StockUpdateEvent;
import com.flowcart.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockUpdateListener {

    private final ProductService productService;

    @RabbitListener(queues = RabbitMQConfig.STOCK_UPDATE_QUEUE)
    public void handleStockUpdate(StockUpdateEvent event) {
        log.info("Received stock update event for product {} - quantity {}", 
                event.getProductId(), event.getQuantity());
        productService.decrementStock(event.getProductId(), event.getQuantity());
    }
}