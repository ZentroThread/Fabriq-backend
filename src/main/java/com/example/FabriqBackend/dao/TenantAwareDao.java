package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.TenantAwareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface TenantAwareDao <T extends TenantAwareEntity, ID> extends JpaRepository<T, ID> {

    @Query("SELECT e FROM #{#entityName} e WHERE e.tenantId = ?#{T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant()}")
    List<T> findAll();

    @Query("SELECT e FROM #{#entityName} e WHERE e.id = ?1 AND e.tenantId = ?#{T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant()}")
    Optional<T> findById(ID id);
}
