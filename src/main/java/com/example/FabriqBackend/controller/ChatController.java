package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatController {
    
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        // 1. Set timestamp if not present
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(LocalDateTime.now());
        }
        
        // No DB save - ephemeral messaging only

        // 2. Broadcast to receiver
        messagingTemplate.convertAndSend("/topic/" + chatMessage.getReceiverRole(), chatMessage);
        
        // 3. Broadcast back to sender (echo) so they see their own message
        // only if sender and receiver are different roles (to avoid double receiving if subscribed to both)
        if (!chatMessage.getSenderRole().equals(chatMessage.getReceiverRole())) {
            messagingTemplate.convertAndSend("/topic/" + chatMessage.getSenderRole(), chatMessage);
        }
    }
}
