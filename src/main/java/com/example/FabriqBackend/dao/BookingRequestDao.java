package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRequestDao extends JpaRepository<BookingRequest,Long> {
    List<BookingRequest> findByTenantId(String tenantId);
    List<BookingRequest> findByUserEmail(String userEmail);
}
