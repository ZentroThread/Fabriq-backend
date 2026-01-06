package com.example.FabriqBackend.service.kafka;

@Service
public class NotificationPublisher {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void publishNotification(NotificationEvent event) {
        redisTemplate.convertAndSend("notification-channel", event);
    }
}
