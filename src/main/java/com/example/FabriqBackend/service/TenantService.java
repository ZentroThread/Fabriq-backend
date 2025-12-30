package com.example.FabriqBackend.service;

import com.example.FabriqBackend.model.Tenant;

import java.util.List;
import java.util.Optional;

public interface TenantService {
    
    /**
     * Get tenant by ID
     */
    Optional<Tenant> getTenantById(String tenantId);
    
    /**
     * Get active tenant by ID
     */
    Optional<Tenant> getActiveTenant(String tenantId);
    
    /**
     * Get current tenant info (from TenantContext)
     */
    Optional<Tenant> getCurrentTenantInfo();
    
    /**
     * Update tenant information
     */
    Tenant updateTenant(String tenantId, Tenant tenant);
    
    /**
     * Activate/Deactivate tenant
     */
    void setTenantActive(String tenantId, boolean active);
    
    /**
     * Get all tenants (admin only)
     */
    List<Tenant> getAllTenants();
    
    /**
     * Create new tenant
     */
    Tenant createTenant(Tenant tenant);
}
