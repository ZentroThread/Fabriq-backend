package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.CustomerUpdateDto;
import com.example.FabriqBackend.model.Customer;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICustomerService {

    ResponseEntity<?> addCustomer(Customer customer);
    ResponseEntity<?> deleteCustomer(Integer custId);
    ResponseEntity<?> updateCustomer(Integer custId, CustomerUpdateDto customerUpdateDto);
    Customer getCustomerById(Integer custId);
    List<Customer> getAllCustomers();
}
