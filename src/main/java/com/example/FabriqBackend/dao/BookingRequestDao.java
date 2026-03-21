package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRequestDao extends JpaRepository<BookingRequest,Long> {
    List<BookingRequest> findByTenantId(String tenantId);
    List<BookingRequest> findByUserEmail(String userEmail);
}
