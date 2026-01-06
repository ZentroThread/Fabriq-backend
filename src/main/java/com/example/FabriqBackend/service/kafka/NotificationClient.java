package com.example.FabriqBackend.service.kafka;

import com.example.FabriqBackend.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
public class NotificationClient {
    private final RestTemplate restTemplate;

@RequiredArgsConstructor
@Slf4j
                "http://notification-service:8081/api/notifications/send",

    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;

    @Value("${kafka.topic.notifications:notifications}")
    private String notificationTopic;
