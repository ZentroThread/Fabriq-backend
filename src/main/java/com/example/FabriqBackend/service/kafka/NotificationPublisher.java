package com.example.FabriqBackend.service.kafka;

import com.example.FabriqBackend.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public void publishNotification(NotificationRequest event) {
        redisTemplate.convertAndSend("notification-channel", event);
    }
}