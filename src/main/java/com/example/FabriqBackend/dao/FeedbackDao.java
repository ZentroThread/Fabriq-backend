package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackDao extends JpaRepository<Feedback, Long> {
    List<Feedback> findByApprovedTrue();
}
