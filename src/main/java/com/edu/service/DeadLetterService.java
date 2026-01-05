package com.edu.service;

import com.edu.model.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DeadLetterService {

    @KafkaListener(
            topics = "ORDER_CREATED.DLT",
            groupId = "dlt-monitor-group",
            containerFactory = "simpleFactory"
    )
    public void processFailedMessages(
            OrderEvent event,
            // FIX: Use the actual DLT header name found in your logs
            @Header(name = "kafka_dlt-exception-message", required = false) String errorMessage) {
        try {
            log.error("CRITICAL: Message sent to DLT for Order #{} due to: {}",
                    event.getOrderId(), errorMessage != null ? errorMessage : "Unknown Error");
        } catch (Exception e) {
            log.error("DLT Logging failed: {}", e.getMessage());
        }
    }
}
