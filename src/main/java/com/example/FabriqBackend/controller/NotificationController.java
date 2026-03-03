package com.example.FabriqBackend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.FabriqBackend.service.kafka.NotificationClient;

import java.util.Map;

@RestController
@RequestMapping("/v1/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationClient notificationClient;

    @PostMapping("/publish")
    public ResponseEntity<?> publish(@RequestBody Map<String, Object> payload) {
        try {
            notificationClient.sendNotification(payload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to publish notification: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
