package com.example.FabriqBackend.service.RAG;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

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

        System.out.println("Sending request to RAG: " + body);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        System.out.println("RAG response: " + response.getBody());

        return response.getBody().get("answer").toString();
    }
}