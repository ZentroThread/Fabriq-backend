package com.example.FabriqBackend.service.kafka;

import com.example.FabriqBackend.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationClient {

    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;

    @Value("${kafka.topic.notifications:notification-events}")
    private String notificationTopic;

    public void sendNotification(NotificationRequest request) {
        try {
            kafkaTemplate.send(notificationTopic, request.getUserId(), request);
            log.info("Notification sent to Kafka: {}", request);
        } catch (Exception ex) {
            log.error("Failed to send notification: {}", ex.getMessage());
        }
    }
}