package com.example.FabriqBackend.service.impl;

import lombok.extern.slf4j.Slf4j;

import com.example.FabriqBackend.dao.FeedbackDao;
import com.example.FabriqBackend.model.Feedback;
import com.example.FabriqBackend.service.IFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class FeedbackService implements IFeedbackService {

    private final FeedbackDao feedbackRepository;

    @Override
    public Feedback saveFeedback(String message, int rating, String email) {
        Feedback feedback = new Feedback();
        feedback.setMessage(message);
        feedback.setRating(rating);
        feedback.setUserEmail(email);
        feedback.setApproved(false);
        feedback.setCreatedAt(LocalDateTime.now());

        return feedbackRepository.save(feedback);
    }

    @Override
    public List<Feedback> getApprovedFeedbacks() {
        return feedbackRepository.findByApprovedTrue();
    }

    @Override
    public Feedback approveFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
        feedback.setApproved(true);
        return feedbackRepository.save(feedback);
    }

    @Override
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    @Override
    public void removeFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
}
