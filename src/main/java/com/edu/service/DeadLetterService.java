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

    // CHANGE: Use a unique groupId to separate this from the main retry logic
    @KafkaListener(topics = "ORDER_CREATED.DLT", groupId = "dlt-monitor-group")
    public void processFailedMessages(OrderEvent event, @Header(KafkaHeaders.EXCEPTION_MESSAGE) String errorMessage) {
        try {
            log.error("CRITICAL: Message sent to DLT for Order #{} due to: {}",
                    event.getOrderId(), errorMessage);
            // This service just logs; do NOT throw any exceptions here
        } catch (Exception e) {
            log.error("DLT Logging failed: {}", e.getMessage());
        }
    }

}
