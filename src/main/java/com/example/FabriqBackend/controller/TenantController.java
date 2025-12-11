package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.model.Tenant;
import com.example.FabriqBackend.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tenant")
@RequiredArgsConstructor
@Tag(name = "Tenant Management", description = "APIs for managing tenant information")
public class TenantController {

    private final TenantService tenantService;

    @GetMapping("/current")
    @Operation(
        summary = "Get current tenant information",
        description = "Returns the tenant information for the currently authenticated user"
    )
    public ResponseEntity<Tenant> getCurrentTenant() {
        return tenantService.getCurrentTenantInfo()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{tenantId}")
    @Operation(
        summary = "Get tenant by ID",
        description = "Returns tenant information by tenant ID"
    )
    public ResponseEntity<Tenant> getTenantById(@PathVariable String tenantId) {
        return tenantService.getTenantById(tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{tenantId}")
    @Operation(
        summary = "Update tenant information",
        description = "Updates tenant details like name, email, phone, address etc."
    )
    public ResponseEntity<Tenant> updateTenant(
            @PathVariable String tenantId,
            @RequestBody Tenant tenant
    ) {
        try {
            Tenant updated = tenantService.updateTenant(tenantId, tenant);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/current")
    @Operation(
        summary = "Update current tenant information",
        description = "Updates the tenant information for the currently authenticated user's tenant"
    )
    public ResponseEntity<Tenant> updateCurrentTenant(@RequestBody Tenant tenant) {
        return tenantService.getCurrentTenantInfo()
                .map(currentTenant -> {
                    Tenant updated = tenantService.updateTenant(currentTenant.getTenantId(), tenant);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
