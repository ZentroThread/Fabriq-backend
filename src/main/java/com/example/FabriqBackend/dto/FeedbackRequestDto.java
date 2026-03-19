package com.example.FabriqBackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRequestDto {
    private String message;
    private int rating;
}
