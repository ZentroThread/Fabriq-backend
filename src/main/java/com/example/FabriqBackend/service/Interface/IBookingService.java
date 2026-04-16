package com.example.FabriqBackend.service.Interface;

import com.example.FabriqBackend.model.BookingRequest;

import java.time.LocalDate;
import java.util.List;

public interface IBookingService {
        void createBookingRequest(String tenantId, Long attireId, String userEmail, LocalDate startDate, LocalDate endDate);
        void approveBookingRequest(Long requestId);
        void rejectBookingRequest(Long requestId);
        List<BookingRequest> getBookingRequestsForTenant(String tenantId);
        List<BookingRequest> getBookingRequestsForUser(String userEmail);
        void deleteBookingRequest(Long requestId);
}
