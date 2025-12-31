package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.AdvancePaymentDao;
import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dto.salary.AdvancePaymentRequestDTO;
import com.example.FabriqBackend.dto.salary.AdvancePaymentResponseDTO;
import com.example.FabriqBackend.mapper.AdvancePaymentMapper;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.AdvancePayment;
import com.example.FabriqBackend.service.IAdvancePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdvancePaymentServiceImpl implements IAdvancePaymentService {

    private final AdvancePaymentDao advancePaymentDao;
    private final EmployeeDao employeeDao;

    @Override
    public AdvancePaymentResponseDTO createAdvancePayment(AdvancePaymentRequestDTO requestDTO) {

        Employee employee = employeeDao.findById(requestDTO.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + requestDTO.getEmpId()));

        AdvancePayment advancePayment = AdvancePaymentMapper.toEntity(requestDTO);
        advancePayment.setEmployee(employee);
        advancePaymentDao.save(advancePayment);

        return AdvancePaymentMapper.toDto(advancePayment);
    }

    @Override
    public List<AdvancePaymentResponseDTO> getAdvancePaymentsByEmployeeId(Long empId) {

        employeeDao.findById(empId).orElseThrow(() -> new RuntimeException("Employee not found with id: " + empId));
        List<AdvancePayment> payments = advancePaymentDao.findByEmployeeId(empId);

        return payments.stream().map(AdvancePaymentMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<AdvancePaymentResponseDTO> getAllAdvancePayments() {
        List<AdvancePayment> payments = advancePaymentDao.findAll();
        return payments.stream().map(AdvancePaymentMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteAdvancePayment(Long id) {
        advancePaymentDao.deleteById(id);
    }

    @Override
    public AdvancePaymentResponseDTO updateAdvancePayment(Long id, AdvancePaymentRequestDTO requestDTO) {

        AdvancePayment existingPayment = advancePaymentDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Advance Payment not found with id: " + id));
        AdvancePayment updatedPayment = AdvancePaymentMapper.toEntity(requestDTO);
        updatedPayment.setEmployee(existingPayment.getEmployee());
        updatedPayment.setId(existingPayment.getId());
        advancePaymentDao.save(updatedPayment);

        return AdvancePaymentMapper.toDto(updatedPayment);
    }

    @Override
    public List<AdvancePaymentResponseDTO> getAdvancePaymentsByEmployeeIdAndDateRange(Long empId,String startDate, String endDate) {

        employeeDao.findById(empId).orElseThrow(() -> new RuntimeException("Employee not found with id: " + empId));

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        List<AdvancePayment> advancePayment = advancePaymentDao.findByEmployeeIdAndDateBetween(empId, start, end);
        if(advancePayment.isEmpty()){
            throw new RuntimeException("No Advance Payments found for Employee id: " + empId + " in the given date range.");
        }

        return  advancePayment.stream().map(AdvancePaymentMapper::toDto).collect(Collectors.toList());
    }
}
