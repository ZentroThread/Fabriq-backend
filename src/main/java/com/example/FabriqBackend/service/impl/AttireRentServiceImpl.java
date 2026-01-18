package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.AttireDao;
import com.example.FabriqBackend.dao.AttireRentDao;
import com.example.FabriqBackend.dao.CustomerDao;
import com.example.FabriqBackend.dto.AttireRentAddDto;
import com.example.FabriqBackend.model.Attire;
import com.example.FabriqBackend.model.AttireRent;
import com.example.FabriqBackend.model.Customer;
import com.example.FabriqBackend.service.IAttireRentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "attireRents")
public class AttireRentServiceImpl implements IAttireRentService {

    private final AttireRentDao attireRentDao;
    private final ModelMapper modelMapper;
    private final CustomerDao customerDao;
    private final AttireDao attireDao;

    @CachePut(key = "'attire added for rent.'")
    public ResponseEntity<?> addAttireRent(AttireRentAddDto attireRentAddDto) {
        AttireRent attireRent = modelMapper.map(attireRentAddDto, AttireRent.class);
        Customer customer = customerDao.findByCustCode(attireRentAddDto.getCustomerCode()).orElse(null);
        if (customer == null) {
            return new ResponseEntity<>("Customer not found", HttpStatus.NOT_FOUND);
        }
        attireRent.setCustomer(customer);

        Attire attire = attireDao.findByAttireCode(attireRentAddDto.getAttireCode());
        if (attire == null) {
            return new ResponseEntity<>("Attire not found", HttpStatus.NOT_FOUND);
        }
        attireRent.setAttire(attire);

        attireRentDao.save(attireRent);
        return new ResponseEntity<>(attireRent, HttpStatus.CREATED);
    }

    //@Cacheable(key = "T(com.example.FabriqBackend.tenant.TenantContext).getCurrentTenantId() + ':all'")
    public List<com.example.FabriqBackend.dto.AttireRentDto> getAllAttireRent() {
        List<AttireRent> rents = attireRentDao.findAll();

        // Convert to DTO to avoid LocalDateTime serialization issue
        List<com.example.FabriqBackend.dto.AttireRentDto> dtoList = rents.stream().map(r -> {
            com.example.FabriqBackend.dto.AttireRentDto dto = new com.example.FabriqBackend.dto.AttireRentDto();
            dto.setId(r.getId());
            dto.setAttireCode(r.getAttireCode() != null ? r.getAttireCode() : (r.getAttire() != null ? r.getAttire().getAttireCode() : null));
            dto.setCustCode(r.getCustCode() != null ? r.getCustCode() : (r.getCustomer() != null ? r.getCustomer().getCustCode() : null));
            dto.setBillingCode(r.getBillingCode() != null ? r.getBillingCode() : (r.getBilling() != null ? r.getBilling().getBillingCode() : null));
            dto.setRentDuration(r.getRentDuration());
            dto.setRentDate(r.getRentDate() != null ? r.getRentDate().toString() : null);
            dto.setReturnDate(r.getReturnDate() != null ? r.getReturnDate().toString() : null);
            return dto;
        }).collect(Collectors.toList());

        return dtoList;
    }

    @CacheEvict(key = "'deleteAttireRent'")
    public ResponseEntity<?> deleteAttireRent(Integer id) {
        attireRentDao.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @CachePut(key = "'updateAttireRent:' + #id")
    public ResponseEntity<?> updateAttireRent(Integer id, AttireRentAddDto dto) {

        AttireRent existing = attireRentDao.findById(id)
                .map(attireRent -> {
                    modelMapper.map(dto, attireRent);

                    Customer customer = customerDao.findByCustCode(dto.getCustomerCode()).orElse(null);
                    if (customer == null) {
                        throw new RuntimeException("Customer not found");
                    }
                    attireRent.setCustomer(customer);

                    Attire attire = attireDao.findByAttireCode(dto.getAttireCode());
                    if (attire == null) {
                        throw new RuntimeException("Attire not found");
                    }
                    attireRent.setAttire(attire);

                    return attireRent;
                }).orElse(null);
        if (existing == null) {
            return new ResponseEntity<>("AttireRent not found", HttpStatus.NOT_FOUND);
        }
        AttireRent updated = attireRentDao.save(existing);

        return ResponseEntity.ok(updated);
    }

    public ResponseEntity<?> getAttireRentsByBillingCode(String billingCode) {
        if (billingCode == null) return ResponseEntity.badRequest().body("billingCode required");
        List<AttireRent> rents = attireRentDao.findAllByBillingCode(billingCode.trim());
        List<com.example.FabriqBackend.dto.AttireRentDto> list = rents.stream().map(r -> {
            com.example.FabriqBackend.dto.AttireRentDto dto = new com.example.FabriqBackend.dto.AttireRentDto();
            dto.setId(r.getId());
            dto.setAttireCode(r.getAttireCode() != null ? r.getAttireCode() : (r.getAttire() != null ? r.getAttire().getAttireCode() : null));
            dto.setCustCode(r.getCustCode() != null ? r.getCustCode() : (r.getCustomer() != null ? r.getCustomer().getCustCode() : null));
            dto.setBillingCode(r.getBillingCode() != null ? r.getBillingCode() : (r.getBilling() != null ? r.getBilling().getBillingCode() : null));
            dto.setRentDuration(r.getRentDuration());
            dto.setRentDate(r.getRentDate() != null ? r.getRentDate().toString() : null);
            dto.setReturnDate(r.getReturnDate() != null ? r.getReturnDate().toString() : null);
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    public ResponseEntity<?> getStatsByAttireCode(String attireCode) {
        if (attireCode == null || attireCode.trim().isEmpty())
            return ResponseEntity.badRequest().body("attireCode required");

        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        List<AttireRent> all = attireRentDao.findAllByAttireCode(attireCode.trim());

        long rentalCount = all.stream()
                .filter(r -> r.getRentDate() != null && !r.getRentDate().isAfter(now))
                .map(r -> r.getCustCode() != null ? r.getCustCode() : (r.getCustomer() != null ? r.getCustomer().getCustCode() : null))
                .filter(java.util.Objects::nonNull)
                .distinct()
                .count();

        List<com.example.FabriqBackend.dto.AttireRentDto> wishlist = all.stream()
                .filter(r -> r.getRentDate() != null && r.getRentDate().isAfter(now))
                .map(r -> {
                    com.example.FabriqBackend.dto.AttireRentDto dto = new com.example.FabriqBackend.dto.AttireRentDto();
                    dto.setId(r.getId());
                    dto.setAttireCode(r.getAttireCode() != null ? r.getAttireCode() : (r.getAttire() != null ? r.getAttire().getAttireCode() : null));
                    dto.setCustCode(r.getCustCode() != null ? r.getCustCode() : (r.getCustomer() != null ? r.getCustomer().getCustCode() : null));
                    dto.setBillingCode(r.getBillingCode() != null ? r.getBillingCode() : (r.getBilling() != null ? r.getBilling().getBillingCode() : null));
                    dto.setRentDate(r.getRentDate() != null ? r.getRentDate().toString() : null);
                    dto.setReturnDate(r.getReturnDate() != null ? r.getReturnDate().toString() : null);
                    return dto;
                }).collect(Collectors.toList());

        java.util.Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("rentalCount", rentalCount);
        resp.put("wishlistCount", wishlist.size());
        resp.put("wishlist", wishlist);

        return ResponseEntity.ok(resp);
    }

}
