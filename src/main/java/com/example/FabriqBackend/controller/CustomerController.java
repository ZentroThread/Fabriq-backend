package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.CustomerUpdateDto;
import com.example.FabriqBackend.model.Customer;
import com.example.FabriqBackend.service.ICustomerService;
import com.example.FabriqBackend.service.impl.CustomerServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/customer")// Base URL for customer-related operations
@RequiredArgsConstructor
public class CustomerController {


    private final ICustomerService customerService;

    @PostMapping("/add-customer")
    @Operation(
            summary = "Add a new customer",
            description = "This endpoint allows adding a new customer by providing the necessary details in the request body."
    )
    public ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
        return customerService.addCustomer(customer);
    }

    @GetMapping("/read-customers")
    @Operation(
            summary = "Retrieve all customers",
            description = "This endpoint retrieves a list of all customers."
    )
    public List<Customer> readCustomers() {

        return customerService.getAllCustomers();
    }

    @DeleteMapping("/delete-customer/{custId}")
    @Operation(
            summary = "Delete a customer",
            description = "This endpoint allows deleting a customer by their ID."
    )
    public ResponseEntity<?> deleteCustomer(@PathVariable Integer custId) {

        return customerService.deleteCustomer(custId);
    }

    @PutMapping("/updateCustomer/{custId}")
    @Operation(
            summary = "Update a customer's details",
            description = "This endpoint allows updating the details of an existing customer by their ID."
    )
    public ResponseEntity<?> updateCustomer(@PathVariable Integer custId , @RequestBody CustomerUpdateDto customerUpdateDto) {
        return customerService.updateCustomer(custId,customerUpdateDto);
    }
    @GetMapping("/{custId}")
    @Operation(
            summary = "Get customer by ID",
            description = "This endpoint retrieves a customer's details by their ID."
    )
    public Customer getCustomerById(@PathVariable Integer custId) {
        return customerService.getCustomerById(custId);
    }
}
