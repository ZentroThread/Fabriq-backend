package com.example.FabriqBackend.service.impl;

import lombok.extern.slf4j.Slf4j;

import com.example.FabriqBackend.dao.FeedbackDao;
import com.example.FabriqBackend.exception.ResourceNotFoundException;
import com.example.FabriqBackend.model.Feedback;
import com.example.FabriqBackend.service.Interface.IFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "feedbacks")
public class FeedbackService implements IFeedbackService {

    private final FeedbackDao feedbackRepository;

    @Override
    @Caching(evict = {
        @CacheEvict(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allFeedbacks'"),
        @CacheEvict(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':approvedFeedbacks'")
    })
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
    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':approvedFeedbacks'")
    public List<Feedback> getApprovedFeedbacks() {
        return feedbackRepository.findByApprovedTrue();
    }

    @Override
    @Caching(evict = {
        @CacheEvict(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allFeedbacks'"),
        @CacheEvict(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':approvedFeedbacks'")
    })
    public Feedback approveFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", String.valueOf(id)));
        feedback.setApproved(true);
        return feedbackRepository.save(feedback);
    }

    @Override
    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allFeedbacks'")
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    @Override
    @Caching(evict = {
        @CacheEvict(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allFeedbacks'"),
        @CacheEvict(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':approvedFeedbacks'")
    })
    public void removeFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
}
