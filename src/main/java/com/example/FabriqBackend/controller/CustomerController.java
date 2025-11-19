package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.CustomerUpdateDto;
import com.example.FabriqBackend.model.Customer;
import com.example.FabriqBackend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")// Base URL for customer-related operations
@RequiredArgsConstructor
public class CustomerController {


    private final CustomerService customerService;

    @PostMapping("/add-customer")
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
        return customerService.addCustomer(customer);
    }

    @GetMapping("/rea-customers")
    public List<Customer> readCustomers() {

        return customerService.getAllCustomers();
    }

    @DeleteMapping("/delete-customer/{custId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Integer custId) {
        return customerService.deleteCustomer(custId);
    }

    @PutMapping("/update-customer/{custId}")
    public ResponseEntity<?> updateCustomer(@PathVariable Integer custId, @RequestBody CustomerUpdateDto customerUpdateDto) {
        return customerService.updateCustomer(custId, customerUpdateDto);
    }

    @GetMapping("/{custId}")
    public Customer getCustomerById(@PathVariable Integer custId) {
        return customerService.getCustomerById(custId);
    }
}
