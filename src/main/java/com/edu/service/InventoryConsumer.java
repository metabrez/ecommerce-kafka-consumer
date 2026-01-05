package com.edu.service;

import com.edu.model.InventoryResponse;
import com.edu.model.OrderEvent;
import com.edu.repo.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryConsumer {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "ORDER_CREATED", groupId = "inventory-group-v2")
    public void handleOrderEvent(OrderEvent event) {
        log.info("-> Kafka Message Received: Processing Order #{}", event.getOrderId());
        boolean allUpdated = true;

        for (Long pid : event.getProductIds()) {
            int updated = inventoryRepository.reduceStock(pid, 1);
            if (updated == 0) {
                allUpdated = false;
                break;
            }
        }

        if (!allUpdated) {
            // Send failure message back to Order Service
            InventoryResponse response = new InventoryResponse(event.getOrderId(), "FAILED");
            kafkaTemplate.send("inventory_response_topic", response);
            throw new RuntimeException("Inventory failed for Order #" + event.getOrderId());
        }
    }
}