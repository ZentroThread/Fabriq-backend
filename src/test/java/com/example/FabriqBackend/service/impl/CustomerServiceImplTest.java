package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.CustomerDao;
import com.example.FabriqBackend.dto.CustomerUpdateDto;
import com.example.FabriqBackend.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerDao customerDao;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    // ---------- ADD CUSTOMER ----------

    @Test
    void shouldAddCustomerSuccessfully() {
        Customer customer = new Customer();
        customer.setCustId(1);

        when(customerDao.save(customer)).thenReturn(customer);

        ResponseEntity<?> response = customerService.addCustomer(customer);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(customer, response.getBody());
        verify(customerDao).save(customer);
    }

    // ---------- GET ALL CUSTOMERS ----------

    @Test
    void shouldReturnAllCustomers() {
        when(customerDao.findAll())
                .thenReturn(List.of(new Customer(), new Customer()));

        List<Customer> customers = customerService.getAllCustomers();

        assertEquals(2, customers.size());
        verify(customerDao).findAll();
    }

    // ---------- DELETE CUSTOMER ----------

    @Test
    void shouldDeleteCustomerSuccessfully() {
        doNothing().when(customerDao).deleteById(1);

        ResponseEntity<?> response = customerService.deleteCustomer(1);

        assertEquals(200, response.getStatusCodeValue());
        verify(customerDao).deleteById(1);
    }

    // ---------- UPDATE CUSTOMER ----------

    @Test
    void shouldUpdateCustomerSuccessfully() {
        Customer existingCustomer = new Customer();
        existingCustomer.setCustId(1);

        CustomerUpdateDto updateDto = new CustomerUpdateDto();

        when(customerDao.findById(1))
                .thenReturn(Optional.of(existingCustomer));
        when(customerDao.save(existingCustomer))
                .thenReturn(existingCustomer);

        ResponseEntity<?> response =
                customerService.updateCustomer(1, updateDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(existingCustomer, response.getBody());
        verify(modelMapper).map(updateDto, existingCustomer);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingCustomer() {
        CustomerUpdateDto updateDto = new CustomerUpdateDto();

        when(customerDao.findById(1))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response =
                customerService.updateCustomer(1, updateDto);

        assertEquals(404, response.getStatusCodeValue());
    }

    // ---------- GET CUSTOMER BY ID ----------

    @Test
    void shouldReturnCustomerByIdSuccessfully() {
        Customer customer = new Customer();
        customer.setCustId(1);

        when(customerDao.findById(1))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerById(1);

        assertNotNull(result);
        assertEquals(1, result.getCustId());
    }

    @Test
    void shouldReturnNullWhenCustomerNotFound() {
        when(customerDao.findById(1))
                .thenReturn(Optional.empty());

        Customer result = customerService.getCustomerById(1);

        assertNull(result);
    }
}
