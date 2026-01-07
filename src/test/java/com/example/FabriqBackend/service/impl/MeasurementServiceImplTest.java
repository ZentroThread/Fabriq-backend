package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.*;
import com.example.FabriqBackend.dto.MeasurementAddDto;
import com.example.FabriqBackend.dto.MeasurementUpdateDto;
import com.example.FabriqBackend.model.*;
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
class MeasurementServiceImplTest {

    @Mock
    private MeasurementDao measurementDao;

    @Mock
    private CustomerDao customerDao;

    @Mock
    private AttireDao attireDao;

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MeasurementServiceImpl measurementService;

    // ---------- CREATE MEASUREMENT ----------

    @Test
    void shouldCreateMeasurementSuccessfully() {
        MeasurementAddDto dto = new MeasurementAddDto();
        dto.setCustCode("C001");
        dto.setAttireCode("A001");
        dto.setCategoryCode("CAT001");

        Measurement measurement = new Measurement();
        Customer customer = new Customer();
        Attire attire = new Attire();
        Category category = new Category();

        when(modelMapper.map(dto, Measurement.class)).thenReturn(measurement);
        when(customerDao.findByCustCode("C001")).thenReturn(Optional.of(customer));
        when(attireDao.findByAttireCode("A001")).thenReturn(attire);
        when(categoryDao.findByCategoryCode("CAT001")).thenReturn(category);
        when(measurementDao.save(measurement)).thenReturn(measurement);

        ResponseEntity<?> response = measurementService.createMeasurements(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Measurements created successfully", response.getBody());
        verify(measurementDao).save(measurement);
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        MeasurementAddDto dto = new MeasurementAddDto();
        dto.setCustCode("C404");

        when(customerDao.findByCustCode("C404")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> measurementService.createMeasurements(dto)
        );

        assertTrue(ex.getMessage().contains("Customer not found"));
    }

    @Test
    void shouldThrowExceptionWhenAttireNotFound() {
        MeasurementAddDto dto = new MeasurementAddDto();
        dto.setCustCode("C001");
        dto.setAttireCode("A404");

        Measurement measurement = new Measurement();

        when(modelMapper.map(dto, Measurement.class))
                .thenReturn(measurement);

        when(customerDao.findByCustCode("C001"))
                .thenReturn(Optional.of(new Customer()));

        when(attireDao.findByAttireCode("A404"))
                .thenReturn(null);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> measurementService.createMeasurements(dto)
        );

        assertTrue(ex.getMessage().contains("Attire not found"));
    }


    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        MeasurementAddDto dto = new MeasurementAddDto();
        dto.setCustCode("C001");
        dto.setAttireCode("A001");
        dto.setCategoryCode("CAT404");

        Measurement measurement = new Measurement();

        when(modelMapper.map(dto, Measurement.class))
                .thenReturn(measurement);

        when(customerDao.findByCustCode("C001"))
                .thenReturn(Optional.of(new Customer()));

        when(attireDao.findByAttireCode("A001"))
                .thenReturn(new Attire());

        when(categoryDao.findByCategoryCode("CAT404"))
                .thenReturn(null);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> measurementService.createMeasurements(dto)
        );

        assertTrue(ex.getMessage().contains("Category not found"));
    }


    // ---------- GET ALL MEASUREMENTS ----------

    @Test
    void shouldReturnAllMeasurements() {
        when(measurementDao.findAll())
                .thenReturn(List.of(new Measurement(), new Measurement()));

        ResponseEntity<?> response = measurementService.getAllMeasurements();

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void shouldReturnNotFoundWhenNoMeasurementsExist() {
        when(measurementDao.findAll()).thenReturn(List.of());

        ResponseEntity<?> response = measurementService.getAllMeasurements();

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void shouldDeleteMeasurementSuccessfully() {
        when(measurementDao.findById(1))
                .thenReturn(Optional.of(new Measurement()));
        doNothing().when(measurementDao).deleteById(1);

        ResponseEntity<?> response = measurementService.deleteMeasurement(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Measurement deleted successfully", response.getBody());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingMeasurement() {
        when(measurementDao.findById(1))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = measurementService.deleteMeasurement(1);

        assertEquals(404, response.getStatusCodeValue());
    }

    // ---------- UPDATE MEASUREMENT ----------

    @Test
    void shouldUpdateMeasurementSuccessfully() {
        Measurement measurement = new Measurement();
        MeasurementUpdateDto updateDto = new MeasurementUpdateDto();

        when(measurementDao.findById(1))
                .thenReturn(Optional.of(measurement));
        when(measurementDao.save(measurement))
                .thenReturn(measurement);

        ResponseEntity<?> response =
                measurementService.updateMeasurement(1, updateDto);

        assertEquals(200, response.getStatusCodeValue());
    }

    // ---------- GET MEASUREMENT BY ID ----------

    @Test
    void shouldReturnMeasurementById() {
        when(measurementDao.findById(1))
                .thenReturn(Optional.of(new Measurement()));

        ResponseEntity<?> response =
                measurementService.getMeasurementById(1);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void shouldReturnNotFoundWhenMeasurementDoesNotExist() {
        when(measurementDao.findById(1))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response =
                measurementService.getMeasurementById(1);

        assertEquals(404, response.getStatusCodeValue());
    }

}
