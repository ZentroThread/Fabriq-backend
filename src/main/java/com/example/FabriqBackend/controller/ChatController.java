package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.model.ChatMessage;
import com.example.FabriqBackend.dao.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository; // Optional: Inject your repo if available

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        // 1. Save to DB
        chatMessage.setTimestamp(java.time.LocalDateTime.now());
        chatMessageRepository.save(chatMessage);
        
        // 2. Broadcast to specific role topic
        // e.g., if receiverRole is "CASHIER", send to "/topic/CASHIER"
        messagingTemplate.convertAndSend("/topic/" + chatMessage.getReceiverRole(), chatMessage);
        
        // Also send back to sender so they see their own message immediately (or handle optimistically in frontend)
        messagingTemplate.convertAndSend("/topic/" + chatMessage.getSenderRole(), chatMessage);
    }
    
    @GetMapping("/api/chat/history/{role}")
    @ResponseBody
    public List<ChatMessage> getChatHistory(@PathVariable String role) {
       return chatMessageRepository.findByReceiverRoleOrderByTimestampDesc(role);
    }
}
