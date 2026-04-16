package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackDao extends JpaRepository<Feedback, Long> {
    List<Feedback> findByApprovedTrue();
}
