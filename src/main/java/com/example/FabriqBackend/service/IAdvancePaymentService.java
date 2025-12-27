package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.salary.AdvancePaymentRequestDTO;
import com.example.FabriqBackend.dto.salary.AdvancePaymentResponseDTO;

import java.util.List;

public interface IAdvancePaymentService {

    AdvancePaymentResponseDTO createAdvancePayment(AdvancePaymentRequestDTO requestDTO);

    List<AdvancePaymentResponseDTO> getAdvancePaymentsByEmployeeId(Long empId);

    List<AdvancePaymentResponseDTO> getAllAdvancePayments();

    void deleteAdvancePayment(Long id);

    AdvancePaymentResponseDTO updateAdvancePayment(Long id, AdvancePaymentRequestDTO requestDTO);

    List<AdvancePaymentResponseDTO> getAdvancePaymentsByEmployeeIdAndDateRange(Long empId,String startDate, String endDate);
}
