package com.edu.config;

import com.edu.model.OrderEvent;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, OrderEvent> consumerFactory,
            KafkaTemplate<String, Object> template) {

        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        // 1. Define Retry logic: 3 attempts, 2-second delay between each
        FixedBackOff backOff = new FixedBackOff(2000L, 3);

        // 2. Define the Dead Letter Publishing Recoverer
       // DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);

        // In KafkaConsumerConfig.java
        // FIX: Tell the recoverer exactly which topic name to use (ORDER_CREATED.DLT)
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (r, e) -> new TopicPartition("ORDER_CREATED.DLT", r.partition()));

        // 3. Set the Error Handler
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        // ADD THIS: Log each retry attempt to see what's happening in your console
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            System.out.println("--- Retry Attempt #" + deliveryAttempt + " for Order Event ---");
        });

        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> simpleFactory(
            ConsumerFactory<String, OrderEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // We do NOT add any ErrorHandler or Retry logic here.
        return factory;
    }
}
