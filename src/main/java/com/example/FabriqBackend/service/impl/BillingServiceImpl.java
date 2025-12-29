package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.BillingDao;
import com.example.FabriqBackend.model.Billing;
import com.example.FabriqBackend.service.IBillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "billings")
public class BillingServiceImpl implements IBillingService {

    private final BillingDao billingDao;

    public ResponseEntity<?> addBilling(Billing billing) {
        billingDao.save(billing);
        return ResponseEntity.ok("Billing record added successfully.");
    }

    public ResponseEntity<?> getAllBillings() {
        String tenatId = TenantContext.getCurrentTenant();
        billingDao.findAllByTenantId(tenatId);
        return ResponseEntity.ok(billingDao.findAllByTenantId(tenatId));
    }
}
