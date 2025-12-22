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

   /* @KafkaListener(topics = "ORDER_CREATED", groupId = "inventory-group")
    public void handleOrderEvent(OrderEvent event) {
        log.info("-> Kafka Message Received: Processing Order #{}", event.getOrderId());

        event.getProductIds().forEach(pid -> {
            int updated = inventoryRepository.reduceStock(pid, 1);

            if (updated > 0) {
                log.info("SUCCESS: Stock for Product {} reduced by 1.", pid);
            } else {
                // CRITICAL: Throw an exception to trigger the DLT logic!
                log.error("FAILURE: Product {} not found. Triggering DLT...", pid);
                throw new RuntimeException("Product ID " + pid + " not found in Inventory DB");
            }
        });
    }*/

    @KafkaListener(topics = "ORDER_CREATED", groupId = "inventory-group")
    public void handleOrderEvent(OrderEvent event) {
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