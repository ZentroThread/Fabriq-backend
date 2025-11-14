package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.CustomerUpdateDto;
import com.example.FabriqBackend.model.Customer;
import com.example.FabriqBackend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController {


    private final CustomerService customerService;

    @PostMapping("/addCustomer")
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
        return customerService.addCustomer(customer);
    }

    @GetMapping("/readCustomers")
    public List<Customer> readCustomers() {

        return customerService.getAllCustomers();
    }

    @DeleteMapping("/deleteCustomer/{custId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Integer custId) {
        System.out.println("Deleting customer...");

        return customerService.deleteCustomer(custId);
    }

    @PutMapping("/updateCustomer/{custId}")
    public ResponseEntity<?> updateCustomer(@PathVariable Integer custId , @RequestBody CustomerUpdateDto customerUpdateDto) {
        return customerService.updateCustomer(custId,customerUpdateDto);
    }

    @GetMapping("/{custId}")
    public Customer getCustomerById(@PathVariable Integer custId) {
        return customerService.getCustomerById(custId);
    }
}
