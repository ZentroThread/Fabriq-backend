package com.example.FabriqBackend.service.RAG;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class RagService {

    public String askRag(String question, String role){

        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:8000/api/chat";

        Map<String,Object> body = new HashMap<>();
        body.put("question", question);
        body.put("role", role);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String,Object>> request = new HttpEntity<>(body, headers);

        log.info("Sending request to RAG service for role: {}, question: {}", role, question);
        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, request, Map.class);

            log.info("Received RAG response successfully.");
            log.debug("RAG response body: {}", response.getBody());

            return response.getBody().get("answer").toString();
        } catch (Exception e) {
            log.error("Error occurred while directly querying the RAG service: ", e);
            throw e;
        }
    }
}