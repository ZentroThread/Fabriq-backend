package com.example.FabriqBackend.listener;

import com.example.FabriqBackend.config.TenantContext;
import com.example.FabriqBackend.model.TenantAwareEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class TenantListner  {
    @PrePersist
    @PreUpdate
    public void setTenant(TenantAwareEntity entity) {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null && (entity.getTenantId() == null || entity.getTenantId().isEmpty())) {
            entity.setTenantId(tenantId);
        }
    }
}
