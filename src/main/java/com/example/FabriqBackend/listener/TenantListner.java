package com.example.FabriqBackend.listener;

import com.example.FabriqBackend.config.TenantContext;
import com.example.FabriqBackend.model.TenantAwareEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class TenantListner  {
    @PrePersist // This method will be called before the entity is persisted
    @PreUpdate // This method will be called before the entity is updated

    public void setTenant(TenantAwareEntity entity) {
        // In a real application, you would get the tenant ID from the security context or session
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            entity.setTenantId(tenantId);
        }
    }
}
