package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.FeedbackRequestDto;
import com.example.FabriqBackend.model.Feedback;
import com.example.FabriqBackend.service.impl.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(
            summary = "Create Feedback",
            description = "Allows authenticated users to submit feedback with a message and rating. The feedback will be saved for approval by an admin."
    )
    @PostMapping
    public String submitFeedback(@RequestBody FeedbackRequestDto request,
                                 Authentication authentication) {

        String email = authentication.getName();

        feedbackService.saveFeedback(
                request.getMessage(),
                request.getRating(),
                email
        );

        return "Feedback submitted for approval";
    }

    @Operation(
            summary = "Get Approved Feedback",
            description = "Retrieves a list of all feedback entries that have been approved by an admin. This endpoint is publicly accessible and does not require authentication."
    )
    @GetMapping("/approved")
    public List<Feedback> getApproved() {
        return feedbackService.getApprovedFeedbacks();
    }

    @Operation(
            summary = "Approve Feedback",
            description = "Allows an admin to approve a specific feedback entry by its ID. Once approved, the feedback will be visible in the list of approved feedback. This endpoint requires admin authentication."
    )
    @PutMapping("/approve/{id}")
    public Feedback approve(@PathVariable Long id) {
        return feedbackService.approveFeedback(id);
    }

    @Operation(
            summary = "Get All Feedback",
            description = "Retrieves a list of all feedback entries, including both approved and unapproved feedback. This endpoint requires admin authentication."
    )
    @GetMapping("/all")
    public List<Feedback> getAll() {
        return feedbackService.getAllFeedback();
    }
    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete Feedback",
            description = "Allows an admin to delete a specific feedback entry by its ID. This endpoint requires admin authentication."
    )
    public void delete(@PathVariable Long id) {
        feedbackService.removeFeedback(id);
    }
}
