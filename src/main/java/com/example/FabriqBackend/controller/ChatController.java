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
        if (chatMessage.getTimestamp() == null) {
            chatMessage.setTimestamp(LocalDateTime.now());
        }

        messagingTemplate.convertAndSend("/topic/" + chatMessage.getReceiverRole(), chatMessage);

        if (!chatMessage.getSenderRole().equals(chatMessage.getReceiverRole())) {
            messagingTemplate.convertAndSend("/topic/" + chatMessage.getSenderRole(), chatMessage);
        }
    }
}
