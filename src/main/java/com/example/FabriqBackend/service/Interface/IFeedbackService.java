package com.example.FabriqBackend.service.Interface;

import com.example.FabriqBackend.model.Feedback;

import java.util.List;

public interface IFeedbackService {
    Feedback saveFeedback(String message, int rating, String email);
    List<Feedback> getApprovedFeedbacks();
    Feedback approveFeedback(Long id);
    List<Feedback> getAllFeedback();
    void removeFeedback(Long id);
}
