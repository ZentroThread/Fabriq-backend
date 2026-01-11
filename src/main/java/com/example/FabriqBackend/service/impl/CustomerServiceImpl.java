package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.CustomerDao;
import com.example.FabriqBackend.dto.CustomerUpdateDto;
import com.example.FabriqBackend.model.Customer;
import com.example.FabriqBackend.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.FabriqBackend.service.kafka.NotificationClient;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "customers")
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerDao customerDao;
    private final ModelMapper modelMapper;
    private final NotificationClient notificationClient;

    @CacheEvict(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allCustomers'")
    public ResponseEntity<?> addCustomer(Customer customer) {
        Customer savedCustomer = customerDao.save(customer);
        // send a simple welcome notification event to Kafka for notification-service
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventId", UUID.randomUUID().toString());
            event.put("eventType", "WELCOME");
            // prefer WhatsApp number if present
            String phone = savedCustomer.getCustWhatsappNumber() != null && !savedCustomer.getCustWhatsappNumber().isBlank()
                    ? savedCustomer.getCustWhatsappNumber()
                    : savedCustomer.getCustMobileNumber();
            event.put("recipientPhone", phone);
            event.put("recipientEmail", savedCustomer.getCustEmail());
            event.put("recipientName", savedCustomer.getCustName());
            Map<String, String> templateData = new HashMap<>();
            templateData.put("custCode", savedCustomer.getCustCode() == null ? "" : savedCustomer.getCustCode());
            templateData.put("custName", savedCustomer.getCustName() == null ? "" : savedCustomer.getCustName());
            event.put("templateData", templateData);
            event.put("priority", 1);
            event.put("timestamp", LocalDateTime.now().toString());

            notificationClient.sendNotification(event);
        } catch (Exception e) {
            // log and continue
            // using lombok logger
            //noinspection AlibabaAvoidPrintStackTrace
            log.error("Failed to publish welcome notification: {}", e.getMessage());
        }
        return ResponseEntity.ok(savedCustomer);
    }

    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allCustomers'")
    public List<Customer> getAllCustomers() {
        String tenantId = TenantContext.getCurrentTenant();
        return customerDao.findAllByTenantId(tenantId);
    }

    @CacheEvict(allEntries = true)
    public ResponseEntity<?> deleteCustomer(Integer custId) {
        customerDao.deleteById(custId);
        return ResponseEntity.ok().build();
    }

    @CacheEvict(allEntries = true)
    public ResponseEntity<?> updateCustomer(Integer custId, CustomerUpdateDto customerUpdateDto) {
        Customer cust = customerDao.findById(custId)
                .map(customer -> {
                    modelMapper.map(customerUpdateDto, customer);
                    return customerDao.save(customer);
                })
                .orElse(null);

        if (cust == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cust);
    }

    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':customer:' + #custId")
    public Customer getCustomerById(Integer custId) {
        return customerDao.findById(custId).orElse(null);
    }
}
