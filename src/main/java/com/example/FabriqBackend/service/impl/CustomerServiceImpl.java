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

    @CachePut(key = "#customer.tenantId + ':' + #customer.custId ")
    public ResponseEntity<?> addCustomer( Customer customer) {
        customerDao.save(customer);
        return ResponseEntity.ok().build();
    }

    @Cacheable(key = "':fetched all customers:'  ")
    public List<Customer> getAllCustomers() {
        return customerDao.findAll();
    }


    @CacheEvict(key = " #custId "   )
    public ResponseEntity<?> deleteCustomer(Integer custId) {
        customerDao.deleteById(custId);
       return ResponseEntity.ok().build();
    }

    @CachePut(key = "':updated customer:' + #custId ")
    public ResponseEntity<?> updateCustomer(Integer custId, CustomerUpdateDto customerUpdateDto) {
        Customer cust = customerDao.findById(custId)
                .map(customer -> {
                    modelMapper.map(customerUpdateDto, customer);

                    Customer updatedCustomer = customerDao.save(customer);
                    return ResponseEntity.ok().body(updatedCustomer);
                })
                .orElseGet(() -> ResponseEntity.notFound().build()).getBody();
        return ResponseEntity.ok(cust);
    }

    @CachePut(key = "':fetched customer by id:' + #custId ")
    public Customer getCustomerById(Integer custId) {
        return customerDao.findById(custId).orElse(null);
    }
}
