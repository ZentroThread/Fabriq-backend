package com.example.FabriqBackend.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String senderId;
    private String senderRole;
    private String receiverRole; 

    private String content;

    private LocalDateTime timestamp = LocalDateTime.now();
    private boolean isRead = false;
}
