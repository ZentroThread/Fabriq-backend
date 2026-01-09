package com.example.FabriqBackend.service.kafka;

import com.example.FabriqBackend.dto.NotificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String messageBody = new String(message.getBody());
            NotificationRequest event = objectMapper.readValue(messageBody, NotificationRequest.class);
            log.info("Received notification: {}", event);
            // Process notification here
        } catch (Exception e) {
            log.error("Error processing notification: {}", e.getMessage());
        }
    }
}