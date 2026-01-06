package com.example.FabriqBackend.service.kafka;

import org.springframework.stereotype.Service;

@Service
public class NotificationSubscriber {

    @RedisListener(topics = "notification-channel")
    public void handleNotification(NotificationEvent event) {
        // Process and send notification via Socket.IO
        socketIOService.sendToClient(event.getUserId(), event.getMessage());
    }
}
