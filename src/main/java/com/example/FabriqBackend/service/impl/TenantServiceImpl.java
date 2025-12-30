package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.TenantDao;
import com.example.FabriqBackend.model.Tenant;
import com.example.FabriqBackend.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantDao tenantDao;

    @Override
    public Optional<Tenant> getTenantById(String tenantId) {
        return tenantDao.findById(Integer.valueOf(tenantId));
    }

    @Override
    public Optional<Tenant> getActiveTenant(String tenantId) {
        return tenantDao.findByTenantIdAndActiveTrue(tenantId);
    }

    @Override
    public Optional<Tenant> getCurrentTenantInfo() {
        String currentTenantId = TenantContext.getCurrentTenant();
        if (currentTenantId == null) {
            return Optional.empty();
        }
        return getActiveTenant(currentTenantId);
    }

    @Override
    @Transactional
    public Tenant updateTenant(String tenantId, Tenant updatedTenant) {
        return tenantDao.findByTenantId(tenantId)
                .map(existing -> {
                    existing.setName(updatedTenant.getName());
                    existing.setBranch(updatedTenant.getBranch());
                    existing.setEmail(updatedTenant.getEmail());
                    existing.setPhoneNumber(updatedTenant.getPhoneNumber());
                    existing.setAddress(updatedTenant.getAddress());
                    existing.setCity(updatedTenant.getCity());
                    return tenantDao.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantId));
    }

    @Override
    @Transactional
    public void setTenantActive(String tenantId, boolean active) {
        tenantDao.findByTenantId(tenantId).ifPresent(tenant -> {
            tenant.setActive(active);
            tenantDao.save(tenant);
        });
    }

    @Override
    public List<Tenant> getAllTenants() {
        return tenantDao.findAll();
    }

    @Override
    @Transactional
    public Tenant createTenant(Tenant tenant) {
        if (tenantDao.existsByEmail(tenant.getEmail())) {
            throw new RuntimeException("Tenant with email already exists");
        }
        return tenantDao.save(tenant);
    }
}
