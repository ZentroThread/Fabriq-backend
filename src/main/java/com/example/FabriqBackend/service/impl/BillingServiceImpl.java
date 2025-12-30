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
        String tenantId = TenantContext.getCurrentTenant();
        billingDao.findAllByTenantId(tenantId);
        return ResponseEntity.ok(billingDao.findAllByTenantId(tenantId));
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

        log.info("Created billing (temp id) for customer {}", customer.getCustCode());

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

            int duration = (int) Math.max(1, ChronoUnit.DAYS.between(start, end));
            rent.setRentDuration(duration);

            total += (attire.getAttirePrice() != null ? attire.getAttirePrice() : 0.0) * duration;

            attireRentDao.save(rent);
        }

        // 4. Update total
        billing.setBillingTotal(String.valueOf(total));
        billingDao.save(billing);

        log.info("Billing {} total updated to {}", billing.getBillingCode(), billing.getBillingTotal());

        return ResponseEntity.ok(billing);
    }

    @Transactional
    public ResponseEntity<?> payBilling(com.example.FabriqBackend.dto.PayBillingDto dto) {
        if (dto == null || dto.getBillingCode() == null) {
            return ResponseEntity.badRequest().body("billingCode required");
        }

        String billingCode = dto.getBillingCode().trim();
        Billing billing = billingDao.findByBillingCode(billingCode);
        if (billing == null) return ResponseEntity.badRequest().body("Billing not found");

        // Find attire rents for this billing (tenant-aware)
        double subtotal = 0.0;
        java.util.List<AttireRent> allRents = attireRentDao.findAll();
        java.util.List<AttireRent> rents = new java.util.ArrayList<>();
        for (AttireRent r : allRents) {
            if (billingCode.equals(r.getBillingCode())) {
                rents.add(r);
                if (r.getAttire() != null && r.getAttire().getAttirePrice() != null) {
                    int days = r.getRentDuration() != null ? r.getRentDuration() : 1;
                    subtotal += r.getAttire().getAttirePrice() * days;
                }
            }
        }

        double discountPerc = dto.getDiscountPercentage() != null ? dto.getDiscountPercentage() : 0.0;
        if (discountPerc < 0) discountPerc = 0.0;
        if (discountPerc > 100) discountPerc = 100.0;

        double discountAmount = subtotal * (discountPerc / 100.0);
        double total = subtotal - discountAmount;

        // Update billing record
        billing.setBillingTotal(String.valueOf(total));
        billing.setBillingStatus("PAID");
        billing.setBillingType(dto.getPaymentMethod());
        billingDao.save(billing);

        log.info("Billing {} marked as PAID ({}).", billing.getBillingCode(), billing.getBillingTotal());

        // Build printable HTML
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta charset='utf-8'><title>Bill</title></head><body style='font-family: Arial, Helvetica, sans-serif; width:300px;'>");
        html.append("<h2 style='text-align:center;margin:0'>Hiru Sandu Bridal Ware</h2>");
        // Customer
        html.append("<p style='margin:4px 0;'>Customer: ")
            .append(billing.getCustomer() != null ? billing.getCustomer().getCustName() : "-")
            .append("</p>");
        // Reference
        html.append("<p style='margin:4px 0;'>Ref: ")
            .append(billing.getBillingCode() != null ? billing.getBillingCode() : "-")
            .append("</p>");
        // Mobile (use correct getter from Customer)
        html.append("<p style='margin:4px 0;'>Mobile: ")
            .append(billing.getCustomer() != null ? (billing.getCustomer().getCustMobileNumber() != null ? billing.getCustomer().getCustMobileNumber() : "-") : "-")
            .append("</p>");
        html.append("<hr/>");
        html.append("<table style='width:100%;font-size:12px;'>");
        html.append("<thead><tr><th style='text-align:left'>Item</th><th style='text-align:right'>Price</th></tr></thead><tbody>");
        for (AttireRent r : rents) {
            String code = r.getAttireCode() != null ? r.getAttireCode() : (r.getAttire() != null ? r.getAttire().getAttireCode() : "-");
            double price = r.getAttire() != null && r.getAttire().getAttirePrice() != null ? r.getAttire().getAttirePrice() : 0.0;
            int days = r.getRentDuration() != null ? r.getRentDuration() : 1;
            double lineTotal = price * days;
            html.append("<tr><td>").append(code).append("</td><td style='text-align:right'>LKR ")
                .append(String.format("%.2f", lineTotal)).append("</td></tr>");
        }
        html.append("</tbody></table>");
        html.append("<hr/>");
        html.append("<p style='display:flex;justify-content:space-between;margin:6px 0;'><span>Subtotal</span><span>LKR ")
            .append(String.format("%.2f", subtotal)).append("</span></p>");
        html.append("<p style='display:flex;justify-content:space-between;margin:6px 0;'><span>Discount (")
            .append(String.format("%.2f", discountPerc)).append("%)</span><span>- LKR ")
            .append(String.format("%.2f", discountAmount)).append("</span></p>");
        html.append("<p style='display:flex;justify-content:space-between;font-weight:bold;margin:6px 0;'><span>Total</span><span>LKR ")
            .append(String.format("%.2f", total)).append("</span></p>");
        html.append("<p style='margin:8px 0;'>Payment Method: ")
            .append(dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "-")
            .append("</p>");
        html.append("<hr/>");
        html.append("<p style='text-align:center;margin-top:12px;'>Thank you</p>");
        html.append("</body></html>");

        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("billing", billing);
        resp.put("items", rents);
        resp.put("billHtml", html.toString());

        return ResponseEntity.ok(resp);
    }
}
