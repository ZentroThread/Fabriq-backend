package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.CustomerDao;
import com.example.FabriqBackend.dto.CustomerUpdateDto;
import com.example.FabriqBackend.model.Customer;
import com.example.FabriqBackend.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "customers")
public class CustomerServiceImpl implements ICustomerService {

    private final CustomerDao customerDao;
    private final ModelMapper modelMapper;

    @CachePut(key = "#result.body != null ? #result.body.tenantId + ':' + #result.body.custId : 'null'", condition = "#result.statusCode.is2xxSuccessful()")
    public ResponseEntity<?> addCustomer(Customer customer) {
        Customer savedCustomer = customerDao.save(customer);
        return ResponseEntity.ok(savedCustomer);
    }

    @Cacheable(key = "'all'")
    public List<Customer> getAllCustomers() {
        return customerDao.findAll();
    }

    @CacheEvict(key = "#custId")
    public ResponseEntity<?> deleteCustomer(Integer custId) {
        customerDao.deleteById(custId);
        return ResponseEntity.ok().build();
    }

    @CachePut(key = "#custId")
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

    @Cacheable(key = "#custId")
    public Customer getCustomerById(Integer custId) {
        return customerDao.findById(custId).orElse(null);
    }
}
