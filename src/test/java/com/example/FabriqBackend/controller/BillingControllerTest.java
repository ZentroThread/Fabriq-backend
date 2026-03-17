package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.config.JwtFilter;
import com.example.FabriqBackend.dto.CreateBillingAndPayDto;
import com.example.FabriqBackend.dto.CreateBillingWithRentalsDto;
import com.example.FabriqBackend.dto.PayBillingDto;
import com.example.FabriqBackend.model.Billing;
import com.example.FabriqBackend.service.IBillingService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BillingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BillingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTService jwtService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private IBillingService billingService;

    @Autowired
    private ObjectMapper objectMapper;


    // 1️⃣ Test Add Billing
    @Test
    void testAddBilling() throws Exception {

        Mockito.when(billingService.addBilling(any()))
                .thenReturn(ResponseEntity.ok().build());

        Billing billing = new Billing();

        mockMvc.perform(post("/v1/billing/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(billing)))
                .andExpect(status().isOk());
    }


    // 2️⃣ Test Get All Billings
    @Test
    void testGetAllBillings() throws Exception {

        Mockito.when(billingService.getAllBillings())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/v1/billing/all"))
                .andExpect(status().isOk());
    }


    // 3️⃣ Test Create Billing With Rentals
    @Test
    void testCreateBillingWithRentals() throws Exception {

        Mockito.when(billingService.createBillingWithRentals(any()))
                .thenReturn(ResponseEntity.ok().build());

        CreateBillingWithRentalsDto dto = new CreateBillingWithRentalsDto();

        mockMvc.perform(post("/v1/billing/create-with-rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }


    // 4️⃣ Test Create Billing And Pay
    @Test
    void testCreateBillingAndPay() throws Exception {

        Mockito.when(billingService.createBillingAndPay(any()))
                .thenReturn(ResponseEntity.ok().build());

        CreateBillingAndPayDto dto = new CreateBillingAndPayDto();

        mockMvc.perform(post("/v1/billing/create-and-pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }


    // 5️⃣ Test Pay Billing
    @Test
    void testPayBilling() throws Exception {

        Mockito.when(billingService.payBilling(any()))
                .thenReturn(ResponseEntity.ok().build());

        PayBillingDto dto = new PayBillingDto();

        mockMvc.perform(post("/v1/billing/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

}