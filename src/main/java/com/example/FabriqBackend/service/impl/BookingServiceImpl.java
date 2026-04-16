package com.example.FabriqBackend.service.impl;

import lombok.extern.slf4j.Slf4j;

import com.example.FabriqBackend.dao.BookingRequestDao;
import com.example.FabriqBackend.dao.CustDao;
import com.example.FabriqBackend.model.BookingRequest;
import com.example.FabriqBackend.model.User;
import com.example.FabriqBackend.service.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class BookingServiceImpl implements IBookingService {

    private final BookingRequestDao bookingRequestRepository;
    private final CustDao customerRepository;

    @Override
    public void createBookingRequest(String tenantId, Long attireId, String userEmail, LocalDate startDate, LocalDate endDate) {

        User user = customerRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        BookingRequest br = new BookingRequest();
        br.setTenantId(tenantId);
        br.setAttireId(attireId);
        br.setUserEmail(userEmail);
        br.setStartDate(startDate);
        br.setEndDate(endDate);
        br.setCustomerName(user.getName());

        bookingRequestRepository.save(br);
    }

    @Override
    public void approveBookingRequest(Long requestId) {
        BookingRequest br = bookingRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Booking request not found with id: " + requestId));
        br.setStatus("APPROVED");
        bookingRequestRepository.save(br);
    }

    @Override
    public void rejectBookingRequest(Long requestId) {
        BookingRequest br = bookingRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Booking request not found with id: " + requestId));
        br.setStatus("REJECTED");
        bookingRequestRepository.save(br);

    }

    @Override
    public List<BookingRequest> getBookingRequestsForTenant(String tenantId) {
        return bookingRequestRepository.findByTenantId(tenantId);
    }

    @Override
    public List<BookingRequest> getBookingRequestsForUser(String userEmail) {
        return bookingRequestRepository.findByUserEmail(userEmail);
    }

    @Override
    public void deleteBookingRequest(Long requestId) {
        bookingRequestRepository.deleteById(requestId);
    }
}
