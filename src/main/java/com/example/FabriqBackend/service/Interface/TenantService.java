package com.example.FabriqBackend.service.Interface;

import com.example.FabriqBackend.model.Tenant;

import java.util.List;
import java.util.Optional;

public interface TenantService {

    Optional<Tenant> getTenantById(String tenantId);

    Optional<Tenant> getActiveTenant(String tenantId);

    Optional<Tenant> getCurrentTenantInfo();

    Tenant updateTenant(String tenantId, Tenant tenant);

    void setTenantActive(String tenantId, boolean active);

    List<Tenant> getAllTenants();

    Tenant createTenant(Tenant tenant);
}
