package com.example.FabriqBackend.model;

import com.example.FabriqBackend.listener.TenantListner;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
@EntityListeners(TenantListner.class)
public abstract  class TenantAwareEntity {

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
}
