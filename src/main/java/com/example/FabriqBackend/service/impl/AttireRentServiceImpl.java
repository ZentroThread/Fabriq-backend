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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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


    @Cacheable(key = "'allAttireRent'")
    public ResponseEntity<?> getAllAttireRent() {
        return new ResponseEntity<>(attireRentDao.findAll(), HttpStatus.OK);
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

}
