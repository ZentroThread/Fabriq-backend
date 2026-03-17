package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.config.JwtFilter;
import com.example.FabriqBackend.dto.CustomerUpdateDto;
import com.example.FabriqBackend.model.Customer;
import com.example.FabriqBackend.service.ICustomerService;
import com.example.FabriqBackend.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTService jwtService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private ICustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;


    // 1️⃣ Test Add Customer
    @Test
    void testAddCustomer() throws Exception {

        Mockito.when(customerService.addCustomer(any()))
                .thenReturn(ResponseEntity.ok().build());

        Customer customer = new Customer();

        mockMvc.perform(post("/v1/customer/add-customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk());
    }


    // 2️⃣ Test Read All Customers
    @Test
    void testReadCustomers() throws Exception {

        Mockito.when(customerService.getAllCustomers())
                .thenReturn(List.of(new Customer()));

        mockMvc.perform(get("/v1/customer/read-customers"))
                .andExpect(status().isOk());
    }


    // 3️⃣ Test Delete Customer
    @Test
    void testDeleteCustomer() throws Exception {

        Mockito.when(customerService.deleteCustomer(1))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/v1/customer/delete-customer/1"))
                .andExpect(status().isOk());
    }


    // 4️⃣ Test Update Customer
    @Test
    void testUpdateCustomer() throws Exception {

        Mockito.when(customerService.updateCustomer(eq(1), any()))
                .thenReturn(ResponseEntity.ok().build());

        CustomerUpdateDto dto = new CustomerUpdateDto();

        mockMvc.perform(put("/v1/customer/updateCustomer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }


    // 5️⃣ Test Get Customer By Id
    @Test
    void testGetCustomerById() throws Exception {

        Mockito.when(customerService.getCustomerById(1))
                .thenReturn(new Customer());

        mockMvc.perform(get("/v1/customer/1"))
                .andExpect(status().isOk());
    }

}