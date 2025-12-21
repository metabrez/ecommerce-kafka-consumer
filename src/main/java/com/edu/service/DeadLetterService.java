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

    @KafkaListener(topics = "ORDER_CREATED.DLT", groupId = "inventory-group")
    public void processFailedMessages(OrderEvent event, @Header(KafkaHeaders.EXCEPTION_MESSAGE) String errorMessage) {
        log.error("CRITICAL: Message sent to DLT for Order #{} due to: {}",
                event.getOrderId(), errorMessage);

        // Potential actions:
        // - Save to a 'failed_orders' table for admin review
        // - Send an alert to Slack/Email
    }
}
