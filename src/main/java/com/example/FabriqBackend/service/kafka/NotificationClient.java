package com.example.FabriqBackend.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationClient {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.notifications:notifications}")
    private String notificationTopic;

    public void sendNotification(Object payload) {
        try {
            String json;
            try {
                json = objectMapper.writeValueAsString(payload);
            } catch (JsonProcessingException jpe) {
                log.error("Failed to serialize notification payload", jpe);
                return;
            }

            kafkaTemplate.send(notificationTopic, null, json);
            log.info("Notification published to Kafka topic {}: {}", notificationTopic, json);
        } catch (Exception ex) {
            log.error("Failed to send notification: {}", ex.getMessage());
        }
    }
}