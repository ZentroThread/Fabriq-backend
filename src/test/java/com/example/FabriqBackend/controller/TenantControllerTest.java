package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.config.JwtFilter;
import com.example.FabriqBackend.model.Tenant;
import com.example.FabriqBackend.service.JWTService;
import com.example.FabriqBackend.service.TenantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TenantController.class)
@AutoConfigureMockMvc(addFilters = false)
class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTService jwtService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private TenantService tenantService;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ 1. Test GET tenant by ID
    @Test
    void testGetTenantById_success() throws Exception {

        Tenant tenant = new Tenant();
        tenant.setTenantId("T001");
        tenant.setName("Test Tenant");

        when(tenantService.getTenantById("T001"))
                .thenReturn(Optional.of(tenant));

        mockMvc.perform(get("/api/v1/tenant/T001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value("T001"))
                .andExpect(jsonPath("$.name").value("Test Tenant"));
    }

    // ❌ 2. Test GET tenant NOT FOUND
    @Test
    void testGetTenantById_notFound() throws Exception {

        when(tenantService.getTenantById("T999"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/tenant/T999"))
                .andExpect(status().isNotFound());
    }

    // ✅ 3. Test UPDATE tenant
    @Test
    void testUpdateTenant_success() throws Exception {

        Tenant input = new Tenant();
        input.setName("Updated Tenant");

        Tenant updated = new Tenant();
        updated.setTenantId("T001");
        updated.setName("Updated Tenant");

        when(tenantService.updateTenant("T001", input))
                .thenReturn(updated);

        mockMvc.perform(put("/api/v1/tenant/T001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Tenant"));
    }

    // ❌ 4. Test UPDATE tenant NOT FOUND
    @Test
    void testUpdateTenant_notFound() throws Exception {

        Tenant input = new Tenant();
        input.setName("Fail");

        when(tenantService.updateTenant("T999", input))
                .thenThrow(new RuntimeException());

        mockMvc.perform(put("/api/v1/tenant/T999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());
    }

    // ✅ 5. Test GET ALL tenants
    @Test
    void testGetAllTenants() throws Exception {

        Tenant t1 = new Tenant();
        t1.setTenantId("T001");
        t1.setName("Tenant 1");

        when(tenantService.getAllTenants())
                .thenReturn(java.util.List.of(t1));

        mockMvc.perform(get("/api/v1/tenant/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tenantId").value("T001"));
    }
}