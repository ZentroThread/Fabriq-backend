package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantDao extends JpaRepository<Tenant, Integer> {

    Optional<Tenant> findByTenantIdAndActiveTrue(String tenantId);

    boolean existsByEmail(String email);
    
    Optional<Tenant> findByEmail(String email);

    Optional<Tenant> findByTenantId(String tenantId);
}
