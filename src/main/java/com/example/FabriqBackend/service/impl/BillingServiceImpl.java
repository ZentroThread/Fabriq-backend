package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.AttireDao;
import com.example.FabriqBackend.dao.AttireRentDao;
import com.example.FabriqBackend.dao.BillingDao;
import com.example.FabriqBackend.dao.CustomerDao;
import com.example.FabriqBackend.dto.AttireRentItemDto;
import com.example.FabriqBackend.dto.CreateBillingWithRentalsDto;
import com.example.FabriqBackend.dto.PayBillingDto;
import com.example.FabriqBackend.model.Attire;
import com.example.FabriqBackend.model.AttireRent;
import com.example.FabriqBackend.model.Billing;
import com.example.FabriqBackend.model.Customer;
import com.example.FabriqBackend.service.IBillingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.TemplateEngine;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "billings")
public class BillingServiceImpl implements IBillingService {


    private final BillingDao billingDao;
    private final CustomerDao customerDao;
    public final AttireDao attireDao;
    public final AttireRentDao attireRentDao;
    public  final TemplateEngine templateEngine;
    private final com.example.FabriqBackend.service.kafka.NotificationClient notificationClient;

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

            total += (attire.getAttirePrice() != null ? attire.getAttirePrice() : 0.0) ;
            System.out.println(total);
            attireRentDao.save(rent);
        }

        // 4. Update total
        billing.setBillingTotal(String.valueOf(total));
        billingDao.save(billing);

        //log.info("Billing {} total updated to {}", billing.getBillingCode(), billing.getBillingTotal());

        return ResponseEntity.ok(billing);
    }

    @Transactional
    public ResponseEntity<?> payBilling(PayBillingDto dto) {
        if (dto == null || dto.getBillingCode() == null) {
            return ResponseEntity.badRequest().body("billingCode required");
        }

        String billingCode = dto.getBillingCode().trim();
        Billing billing = billingDao.findByBillingCode(billingCode);
        if (billing == null) return ResponseEntity.badRequest().body("Billing not found");

        // Find attire rents
        double subtotal = 0.0;
        List<AttireRent> allRents = attireRentDao.findAll();
        List<AttireRent> rents = new ArrayList<>();
        for (AttireRent r : allRents) {
            if (billingCode.equals(r.getBillingCode())) {
                rents.add(r);
                if (r.getAttire() != null && r.getAttire().getAttirePrice() != null) {
                    // Use unit price only (do NOT multiply by rent duration)
                    subtotal += r.getAttire().getAttirePrice();
                }
            }
        }

        double discountPerc = dto.getDiscountPercentage() != null ? dto.getDiscountPercentage() : 0.0;
        discountPerc = Math.max(0, Math.min(100, discountPerc));

        double discountAmount = subtotal * (discountPerc / 100.0);
        double total = subtotal - discountAmount;

        // Update billing
        billing.setBillingTotal(String.valueOf(total));
        billing.setBillingStatus("PAID");
        billing.setBillingType(dto.getPaymentMethod());
        billingDao.save(billing);

        //log.info("Billing {} marked as PAID", billing.getBillingCode());

        // Prepare data for template
        Context context = new Context();
        context.setVariable("customerName",
                billing.getCustomer() != null ? billing.getCustomer().getCustName() : "-");
        context.setVariable("billingCode", billing.getBillingCode());
        context.setVariable("mobile",
                billing.getCustomer() != null ? billing.getCustomer().getCustMobileNumber() : "-");

        // Prepare items for template
        List<Map<String, Object>> itemList = new ArrayList<>();
        for (AttireRent r : rents) {
            Map<String, Object> item = new HashMap<>();
            String code = r.getAttireCode() != null ? r.getAttireCode() :
                    (r.getAttire() != null ? r.getAttire().getAttireCode() : "-");
                double price = r.getAttire() != null && r.getAttire().getAttirePrice() != null ?
                    r.getAttire().getAttirePrice() : 0.0;
                // Show unit price only
                double lineTotal = price;

                item.put("code", code);
                item.put("price", lineTotal);
            itemList.add(item);
        }
        context.setVariable("items", itemList);
        context.setVariable("subtotal", subtotal);
        context.setVariable("discountPerc", discountPerc);
        context.setVariable("discountAmount", discountAmount);
        context.setVariable("total", total);
        context.setVariable("paymentMethod", dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "-");

        // Generate HTML
        String billHtml = templateEngine.process("bill", context);

        Map<String, Object> resp = new HashMap<>();
        resp.put("billing", billing);
        resp.put("items", rents);
        resp.put("billHtml", billHtml);

        // Publish a notification event so the notification-service can send WhatsApp/email
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventId", UUID.randomUUID().toString());
            event.put("eventType", "PAYMENT_CONFIRMED");

            String phone = billing.getCustomer() != null && billing.getCustomer().getCustWhatsappNumber() != null && !billing.getCustomer().getCustWhatsappNumber().isBlank()
                    ? billing.getCustomer().getCustWhatsappNumber()
                    : (billing.getCustomer() != null ? billing.getCustomer().getCustMobileNumber() : null);

            event.put("recipientPhone", phone);
            event.put("recipientEmail", billing.getCustomer() != null ? billing.getCustomer().getCustEmail() : null);
            event.put("recipientName", billing.getCustomer() != null ? billing.getCustomer().getCustName() : null);

            Map<String, String> templateData = new HashMap<>();
            templateData.put("billingCode", billing.getBillingCode() == null ? "" : billing.getBillingCode());
            // Add amount/orderId keys expected by notification-service
            templateData.put("amount", String.valueOf(total));
            templateData.put("orderId", billing.getBillingCode() == null ? "" : billing.getBillingCode());
            templateData.put("total", String.valueOf(total));
            templateData.put("paymentMethod", dto.getPaymentMethod() == null ? "" : dto.getPaymentMethod());

            // Build a short items summary for notifications
            StringBuilder itemsSb = new StringBuilder();
            if (rents != null) {
                for (AttireRent r : rents) {
                    String code = r.getAttireCode() != null ? r.getAttireCode() : (r.getAttire() != null ? r.getAttire().getAttireCode() : "-");
                    double price = r.getAttire() != null && r.getAttire().getAttirePrice() != null ? r.getAttire().getAttirePrice() : 0.0;
                    itemsSb.append(code).append(":Rs.").append((long)price).append("; ");
                }
            }
            templateData.put("items", itemsSb.toString());
            event.put("templateData", templateData);

            event.put("priority", 1);
            event.put("timestamp", LocalDateTime.now().toString());

            notificationClient.sendNotification(event);
        } catch (Exception e) {
            log.error("Failed to publish billing paid notification: {}", e.getMessage());
        }

        return ResponseEntity.ok(resp);
    }
}
