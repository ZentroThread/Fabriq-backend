package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.AttireDao;
import com.example.FabriqBackend.dao.AttireRentDao;
import com.example.FabriqBackend.dao.BillingDao;
import com.example.FabriqBackend.dao.CustomerDao;
import com.example.FabriqBackend.dto.AttireRentItemDto;
import com.example.FabriqBackend.dto.CreateBillingAndPayDto;
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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "billings")
public class BillingServiceImpl implements IBillingService {


    private final BillingDao billingDao;
    private final CustomerDao customerDao;
    public final AttireDao attireDao;
    public final AttireRentDao attireRentDao;
    public final TemplateEngine templateEngine;
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

        log.info("createBillingWithRentals received dto: {}", dto);

        Customer customer = customerDao.findByCustCode(dto.getCustomerCode().trim())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Billing billing = new Billing();
        billing.setCustomer(customer);
        billing = billingDao.save(billing);
        billing = billingDao.save(billing);

        double total = 0;

        for (AttireRentItemDto item : dto.getItems()) {
            log.debug("Received rental item from frontend: attireCode={} rentDate={} returnDate={} rentDateType={}",
                    item.getAttireCode(), item.getRentDate(), item.getReturnDate(), (item.getRentDate() != null ? item.getRentDate().getClass() : "null"));

            Attire attire = attireDao.findByAttireCode(item.getAttireCode().trim());
            if (attire == null) {
                log.warn("Attire not found for code={}; skipping item", item.getAttireCode());
                continue;
            }


            AttireRent rent = new AttireRent();
            rent.setAttire(attire);
            rent.setCustomer(customer);
            rent.setBilling(billing);

            rent.setAttireCode(attire.getAttireCode());
            rent.setCustCode(customer.getCustCode());
            rent.setBillingCode(billing.getBillingCode());

            LocalDateTime start;
            if (item.getRentDate() != null) {
                start = item.getRentDate().atStartOfDay();
            } else {
                start = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
            }

            LocalDateTime end;
            if (item.getReturnDate() != null) {
                end = item.getReturnDate().atTime(23, 59, 59);
            } else {
                end = start.plusDays(3).withHour(23).withMinute(59).withSecond(59);
            }

            rent.setRentDate(start);
            rent.setReturnDate(end);

            int duration = (int) Math.max(1, ChronoUnit.DAYS.between(start, end));
            rent.setRentDuration(duration);

            double price = (attire.getAttirePrice() != null ? attire.getAttirePrice() : 0.0);
            total += price;
            log.debug("Adding rent for attireCode={} price={} runningTotal={}", attire.getAttireCode(), price, total);
            attireRentDao.save(rent);
        }

        billing.setBillingTotal(String.valueOf(total));
        billingDao.save(billing);


        return ResponseEntity.ok(billing);
    }

    @Transactional
    public ResponseEntity<?> createBillingAndPay(CreateBillingAndPayDto dto) {
        log.info("createBillingAndPay received dto: {}", dto);

        // 1. Find customer
        Customer customer = customerDao.findByCustCode(dto.getCustomerCode().trim())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Billing billing = new Billing();
        billing.setCustomer(customer);
        billing = billingDao.save(billing);
        billing = billingDao.save(billing);

        double subtotal = 0.0;

        List<AttireRent> rents = new ArrayList<>();
        for (AttireRentItemDto item : dto.getItems()) {
            log.debug("Received billing item: attireCode={} rentDate={} returnDate={} isCustomItem={} customPrice={}",
                    item.getAttireCode(), item.getRentDate(), item.getReturnDate(), item.getIsCustomItem(), item.getCustomPrice());

            AttireRent rent = new AttireRent();

            if (item.getIsCustomItem() != null && item.getIsCustomItem()) {
                rent.setAttire(null);
                rent.setCustomer(customer);
                rent.setBilling(billing);

                rent.setAttireCode(item.getAttireCode().trim());
                rent.setCustCode(customer.getCustCode());
                rent.setBillingCode(billing.getBillingCode());

                rent.setIsCustomItem(true);
                rent.setCustomItemName(item.getCustomItemName());
                rent.setCustomPrice(item.getCustomPrice());

                LocalDateTime start;
                if (item.getRentDate() != null) {
                    start = item.getRentDate().atStartOfDay();
                } else {
                    start = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
                }

                LocalDateTime end;
                if (item.getReturnDate() != null) {
                    end = item.getReturnDate().atTime(23, 59, 59);
                } else {
                    end = start.plusDays(3).withHour(23).withMinute(59).withSecond(59);
                }

                rent.setRentDate(start);
                rent.setReturnDate(end);

                int duration = (int) Math.max(1, ChronoUnit.DAYS.between(start, end));
                rent.setRentDuration(duration);

                // Use custom price
                double customPrice = item.getCustomPrice() != null ? item.getCustomPrice() : 0.0;
                subtotal += customPrice;

                attireRentDao.save(rent);
                rents.add(rent);
                log.debug("Saved custom rent item for code={} price={}", rent.getAttireCode(), rent.getCustomPrice());

            } else {
                Attire attire = attireDao.findByAttireCode(item.getAttireCode().trim());
                if (attire == null) {
                    log.warn("Attire not found for code={}; skipping", item.getAttireCode());
                    continue;
                }

                rent.setAttire(attire);
                rent.setCustomer(customer);
                rent.setBilling(billing);

                rent.setAttireCode(attire.getAttireCode());
                rent.setCustCode(customer.getCustCode());
                rent.setBillingCode(billing.getBillingCode());

                LocalDateTime start;
                if (item.getRentDate() != null) {
                    start = item.getRentDate().atStartOfDay();
                } else {
                    start = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
                }

                LocalDateTime end;
                if (item.getReturnDate() != null) {
                    end = item.getReturnDate().atTime(23, 59, 59);
                } else {
                    end = start.plusDays(3).withHour(23).withMinute(59).withSecond(59);
                }

                rent.setRentDate(start);
                rent.setReturnDate(end);

                int duration = (int) Math.max(1, ChronoUnit.DAYS.between(start, end));
                rent.setRentDuration(duration);

                double price = (attire.getAttirePrice() != null ? attire.getAttirePrice() : 0.0);
                subtotal += price;
                attireRentDao.save(rent);
                rents.add(rent);
                log.debug("Saved rent item for attireCode={} price={}", attire.getAttireCode(), price);
            }
        }

        double discountPerc = dto.getDiscountPercentage() != null ? dto.getDiscountPercentage() : 0.0;
        discountPerc = Math.max(0, Math.min(100, discountPerc));

        double discountAmount = subtotal * (discountPerc / 100.0);
        double total = subtotal - discountAmount;

        billing.setBillingTotal(String.valueOf(total));
        billing.setBillingStatus("PAID");
        billing.setBillingType(dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "cash");
        billingDao.save(billing);

        log.info("Billing {} created and marked as PAID; total={}", billing.getBillingCode(), total);

        Context context = new Context();
        context.setVariable("customerName", customer.getCustName());
        context.setVariable("billingCode", billing.getBillingCode());
        context.setVariable("mobile", customer.getCustMobileNumber());

        List<Map<String, Object>> itemList = new ArrayList<>();
        for (AttireRent r : rents) {
            Map<String, Object> item = new HashMap<>();
            String code = r.getAttireCode();

            double price;
            if (r.getIsCustomItem() != null && r.getIsCustomItem()) {
                price = r.getCustomPrice() != null ? r.getCustomPrice() : 0.0;
            } else {
                price = r.getAttire() != null && r.getAttire().getAttirePrice() != null ?
                        r.getAttire().getAttirePrice() : 0.0;
            }
            double lineTotal = price;

            String returnDateStr = "-";
            if (r.getReturnDate() != null) {
                returnDateStr = r.getReturnDate().toLocalDate().toString();
            }

            item.put("code", code);
            item.put("price", lineTotal);
            item.put("returnDate", returnDateStr);
            itemList.add(item);
        }
        context.setVariable("items", itemList);
        context.setVariable("subtotal", subtotal);
        context.setVariable("discountPerc", discountPerc);
        context.setVariable("discountAmount", discountAmount);
        context.setVariable("total", total);
        context.setVariable("paymentMethod", dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "cash");

        String billHtml = templateEngine.process("bill", context);

        Map<String, Object> resp = new HashMap<>();
        resp.put("billing", billing);
        resp.put("items", rents);
        resp.put("billHtml", billHtml);

        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventId", UUID.randomUUID().toString());
            event.put("eventType", "PAYMENT_CONFIRMED");

            String phone = customer.getCustWhatsappNumber() != null && !customer.getCustWhatsappNumber().isBlank()
                    ? customer.getCustWhatsappNumber()
                    : customer.getCustMobileNumber();

            event.put("recipientPhone", phone);
            event.put("recipientEmail", customer.getCustEmail());
            event.put("recipientName", customer.getCustName());

            Map<String, String> templateData = new HashMap<>();
            templateData.put("billingCode", billing.getBillingCode());
            templateData.put("amount", String.valueOf(total));
            templateData.put("orderId", billing.getBillingCode());
            templateData.put("total", String.valueOf(total));
            templateData.put("paymentMethod", dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "cash");

            StringBuilder itemsSb = new StringBuilder();
            for (AttireRent r : rents) {
                String code = r.getAttireCode();

                // Check if it's a custom item
                double price;
                if (r.getIsCustomItem() != null && r.getIsCustomItem()) {
                    price = r.getCustomPrice() != null ? r.getCustomPrice() : 0.0;
                } else {
                    price = r.getAttire() != null && r.getAttire().getAttirePrice() != null ?
                            r.getAttire().getAttirePrice() : 0.0;
                }

                itemsSb.append(code).append(":Rs.").append((long) price).append("; ");
            }
            templateData.put("items", itemsSb.toString());
            event.put("templateData", templateData);

            event.put("priority", 1);
            event.put("timestamp", LocalDateTime.now().toString());

            notificationClient.sendNotification(event);
        } catch (Exception e) {
            log.error("Failed to publish billing paid notification: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok(resp);
    }

    @Transactional
    public ResponseEntity<?> payBilling(PayBillingDto dto) {
        if (dto == null || dto.getBillingCode() == null) {
            return ResponseEntity.badRequest().body("billingCode required");
        }

        String billingCode = dto.getBillingCode().trim();
        Billing billing = billingDao.findByBillingCode(billingCode);
        if (billing == null) return ResponseEntity.badRequest().body("Billing not found");

        double subtotal = 0.0;
        List<AttireRent> allRents = attireRentDao.findAll();
        List<AttireRent> rents = new ArrayList<>();
        for (AttireRent r : allRents) {
            if (billingCode.equals(r.getBillingCode())) {
                rents.add(r);
                if (r.getAttire() != null && r.getAttire().getAttirePrice() != null) {
                    subtotal += r.getAttire().getAttirePrice();
                }
            }
        }

        double discountPerc = dto.getDiscountPercentage() != null ? dto.getDiscountPercentage() : 0.0;
        discountPerc = Math.max(0, Math.min(100, discountPerc));

        double discountAmount = subtotal * (discountPerc / 100.0);
        double total = subtotal - discountAmount;

        billing.setBillingTotal(String.valueOf(total));
        billing.setBillingStatus("PAID");
        billing.setBillingType(dto.getPaymentMethod());
        billingDao.save(billing);

        Context context = new Context();
        context.setVariable("customerName",
                billing.getCustomer() != null ? billing.getCustomer().getCustName() : "-");
        context.setVariable("billingCode", billing.getBillingCode());
        context.setVariable("mobile",
                billing.getCustomer() != null ? billing.getCustomer().getCustMobileNumber() : "-");

        List<Map<String, Object>> itemList = new ArrayList<>();
        for (AttireRent r : rents) {
            Map<String, Object> item = new HashMap<>();
            String code = r.getAttireCode() != null ? r.getAttireCode() :
                    (r.getAttire() != null ? r.getAttire().getAttireCode() : "-");
            double price = r.getAttire() != null && r.getAttire().getAttirePrice() != null ?
                    r.getAttire().getAttirePrice() : 0.0;
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

        String billHtml = templateEngine.process("bill", context);

        Map<String, Object> resp = new HashMap<>();
        resp.put("billing", billing);
        resp.put("items", rents);
        resp.put("billHtml", billHtml);

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
            templateData.put("amount", String.valueOf(total));
            templateData.put("orderId", billing.getBillingCode() == null ? "" : billing.getBillingCode());
            templateData.put("total", String.valueOf(total));
            templateData.put("paymentMethod", dto.getPaymentMethod() == null ? "" : dto.getPaymentMethod());
            StringBuilder itemsSb = new StringBuilder();
            if (rents != null) {
                for (AttireRent r : rents) {
                    String code = r.getAttireCode() != null ? r.getAttireCode() : (r.getAttire() != null ? r.getAttire().getAttireCode() : "-");
                    double price = r.getAttire() != null && r.getAttire().getAttirePrice() != null ? r.getAttire().getAttirePrice() : 0.0;
                    itemsSb.append(code).append(":Rs.").append((long) price).append("; ");
                }
            }
            templateData.put("items", itemsSb.toString());
            event.put("templateData", templateData);

            event.put("priority", 1);
            event.put("timestamp", LocalDateTime.now().toString());

            notificationClient.sendNotification(event);
        } catch (Exception e) {
            log.error("Failed to publish billing paid notification: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok(resp);
    }

    @Override
    public ResponseEntity<List<Billing>> getBillingByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Billing> billings = billingDao.findByBillingDateBetween(startDate, endDate);
        return ResponseEntity.ok(billings);
    }
}
