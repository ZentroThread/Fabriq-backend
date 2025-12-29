package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.AttireDao;
import com.example.FabriqBackend.dao.AttireRentDao;
import com.example.FabriqBackend.dao.BillingDao;
import com.example.FabriqBackend.dao.CustomerDao;
import com.example.FabriqBackend.dto.AttireRentItemDto;
import com.example.FabriqBackend.dto.CreateBillingWithRentalsDto;
import com.example.FabriqBackend.model.Attire;
import com.example.FabriqBackend.model.AttireRent;
import com.example.FabriqBackend.model.Billing;
import com.example.FabriqBackend.model.Customer;
import com.example.FabriqBackend.service.IBillingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "billings")
public class BillingServiceImpl implements IBillingService {

    private static final Logger log = LoggerFactory.getLogger(BillingServiceImpl.class);

    private final BillingDao billingDao;
    private final CustomerDao customerDao;
    public final AttireDao attireDao;
    public final AttireRentDao attireRentDao;

    public ResponseEntity<?> addBilling(Billing billing) {
        billingDao.save(billing);
        return ResponseEntity.ok("Billing record added successfully.");
    }

    public ResponseEntity<?> getAllBillings() {
        String tenatId = TenantContext.getCurrentTenant();
        billingDao.findAllByTenantId(tenatId);
        return ResponseEntity.ok(billingDao.findAllByTenantId(tenatId));
    }
    @Transactional
    public ResponseEntity<?> createBillingWithRentals(CreateBillingWithRentalsDto dto) {

        // 1. Find customer
        Customer customer = customerDao.findByCustCode(dto.getCustomerCode().trim())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // 2. Create and save Billing
        Billing billing = new Billing();
        billing.setCustomer(customer);
        billing = billingDao.save(billing);
        billing = billingDao.save(billing); // Save twice to ensure code generation

        double total = 0;

        // 3. Create rentals
        for (AttireRentItemDto item : dto.getItems()) {

            Attire attire = attireDao.findByAttireCode(item.getAttireCode().trim());
            if (attire == null) continue;

            AttireRent rent = new AttireRent();
            rent.setAttire(attire);
            rent.setCustomer(customer);
            rent.setBilling(billing);

            // Set codes from entities ONLY
            rent.setAttireCode(attire.getAttireCode());
            rent.setCustCode(customer.getCustCode());
            rent.setBillingCode(billing.getBillingCode());

            // Dates
            LocalDateTime start = item.getRentDate() != null ? item.getRentDate() : LocalDateTime.now();
            LocalDateTime end = item.getReturnDate() != null ? item.getReturnDate() : start.plusDays(1);
            rent.setRentDate(start);
            rent.setReturnDate(end);

            int duration = (int) Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(start, end));
            rent.setRentDuration(duration);

            total += (attire.getAttirePrice() != null ? attire.getAttirePrice() : 0.0) * duration;

            attireRentDao.save(rent);
        }

        // 4. Update total
        billing.setBillingTotal(String.valueOf(total));
        billingDao.save(billing);

        return ResponseEntity.ok(billing);
    }
}
