package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.TenantDao;
import com.example.FabriqBackend.model.Tenant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceImplTest {

    @Mock
    private TenantDao tenantDao;

    @InjectMocks
    private TenantServiceImpl tenantService;

    private Tenant tenant;

    @BeforeEach
    void setUp() {
        tenant = new Tenant();
        tenant.setTenantId("TENANT1");
        tenant.setName("Test Tenant");
        tenant.setActive(true);
        tenant.setEmail("test@tenant.com");
    }

    @AfterEach
    void clearTenantContext() {
        TenantContext.clear();
    }

    // ---------- READ TESTS ----------

    @Test
    void shouldReturnTenantById() {
        when(tenantDao.findById(1)).thenReturn(Optional.of(tenant));

        Optional<Tenant> result = tenantService.getTenantById("1");

        assertTrue(result.isPresent());
        verify(tenantDao).findById(1);
    }

    @Test
    void shouldReturnActiveTenant() {
        when(tenantDao.findByTenantIdAndActiveTrue("TENANT1"))
                .thenReturn(Optional.of(tenant));

        Optional<Tenant> result = tenantService.getActiveTenant("TENANT1");

        assertTrue(result.isPresent());
        assertEquals("TENANT1", result.get().getTenantId());
        verify(tenantDao).findByTenantIdAndActiveTrue("TENANT1");
    }

    @Test
    void shouldReturnCurrentTenantInfo() {
        TenantContext.setCurrentTenant("TENANT1");
        when(tenantDao.findByTenantIdAndActiveTrue("TENANT1"))
                .thenReturn(Optional.of(tenant));

        Optional<Tenant> result = tenantService.getCurrentTenantInfo();

        assertTrue(result.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenTenantContextIsNull() {
        Optional<Tenant> result = tenantService.getCurrentTenantInfo();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnAllTenants() {
        when(tenantDao.findAll()).thenReturn(List.of(tenant));

        List<Tenant> result = tenantService.getAllTenants();

        assertEquals(1, result.size());
        verify(tenantDao).findAll();
    }

    // ---------- CREATE TESTS ----------

    @Test
    void shouldCreateTenantSuccessfully() {
        when(tenantDao.existsByEmail(tenant.getEmail())).thenReturn(false);
        when(tenantDao.save(tenant)).thenReturn(tenant);

        Tenant result = tenantService.createTenant(tenant);

        assertNotNull(result);
        verify(tenantDao).save(tenant);
    }

    @Test
    void shouldThrowExceptionWhenTenantEmailAlreadyExists() {
        when(tenantDao.existsByEmail(tenant.getEmail())).thenReturn(true);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> tenantService.createTenant(tenant)
        );

        assertEquals("Tenant with email already exists", ex.getMessage());
    }

    // ---------- UPDATE TESTS ----------

    @Test
    void shouldUpdateTenantSuccessfully() {
        when(tenantDao.findByTenantId("TENANT1"))
                .thenReturn(Optional.of(tenant));
        when(tenantDao.save(any(Tenant.class)))
                .thenReturn(tenant);

        Tenant updated = tenantService.updateTenant("TENANT1", tenant);

        assertNotNull(updated);
        verify(tenantDao).save(any(Tenant.class));
    }

    @Test
    void shouldThrowExceptionWhenTenantNotFoundDuringUpdate() {
        when(tenantDao.findByTenantId("TENANT1"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> tenantService.updateTenant("TENANT1", tenant)
        );
    }

    // ---------- STATUS UPDATE TEST ----------

    @Test
    void shouldSetTenantActiveSuccessfully() {
        when(tenantDao.findByTenantId("TENANT1"))
                .thenReturn(Optional.of(tenant));

        tenantService.setTenantActive("TENANT1", false);

        verify(tenantDao).save(tenant);
        assertFalse(tenant.isActive());
    }
}
