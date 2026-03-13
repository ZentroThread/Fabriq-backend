package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.ChatRequest;
import com.example.FabriqBackend.service.RAG.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/rag")
public class RAGController {

    private final RagService ragService;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody ChatRequest req,
                                  Authentication authentication){

        String role = authentication.getAuthorities()
                .iterator().next().getAuthority()
                .replace("ROLE_", "");

        String answer = ragService.askRag(req.getQuestion(), role.toUpperCase());

        System.out.println("User with role " + role + " asked: " + req.getQuestion());

        return ResponseEntity.ok(Map.of("answer", answer));
    }

    @PostMapping("/customer/chat")
    public ResponseEntity<?> CustomerChat(@RequestBody ChatRequest req){

        String answer = ragService.askRag(req.getQuestion(), "CUSTOMER");

        return ResponseEntity.ok(Map.of("answer", answer));
    }
}
