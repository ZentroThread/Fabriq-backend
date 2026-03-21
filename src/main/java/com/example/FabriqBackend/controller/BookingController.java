package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.model.BookingRequest;
import com.example.FabriqBackend.service.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final IBookingService  bookingService;

    @PostMapping("/request")
    public ResponseEntity<String> createBookingRequest(@RequestParam String tenantId,
                                               @RequestParam Long attireId,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, Authentication authentication) {
        String userEmail = authentication.getName();
        bookingService.createBookingRequest(tenantId, attireId,userEmail,startDate, endDate);
        return ResponseEntity.ok("Booking request created successfully");
    }

    @GetMapping("/tenant/{tenantId}")
    public List<BookingRequest> getBookingRequestsForTenant(@PathVariable String tenantId) {
        return bookingService.getBookingRequestsForTenant(tenantId);
    }

    @PutMapping("/{requestId}/approve")
    public ResponseEntity<String> approveBookingRequest(@PathVariable Long requestId) {
        bookingService.approveBookingRequest(requestId);
        return ResponseEntity.ok("Booking request approved successfully");
    }

    @PutMapping("/{requestId}/reject")
    public ResponseEntity<String> rejectBookingRequest(@PathVariable Long requestId) {
        bookingService.rejectBookingRequest(requestId);
        return ResponseEntity.ok("Booking request rejected successfully");
    }

    @GetMapping("/user")
    public List<BookingRequest> getBookingRequestsForUser(Authentication authentication) {
            String authenticatedEmail = authentication.getName();
            return bookingService.getBookingRequestsForUser(authenticatedEmail);
    }
    @DeleteMapping("/delete/{requestId}")
    public ResponseEntity<String> deleteBookingRequest(@PathVariable Long requestId) {
        bookingService.deleteBookingRequest(requestId);
        return ResponseEntity.ok("Booking request deleted successfully");
    }
}
