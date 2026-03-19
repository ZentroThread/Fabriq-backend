package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByReceiverRoleOrderByTimestampDesc(String receiverRole);
}
