package com.example.FabriqBackend.mapper;

import com.example.FabriqBackend.dto.salary.AdvancePaymentRequestDTO;
import com.example.FabriqBackend.dto.salary.AdvancePaymentResponseDTO;
import com.example.FabriqBackend.model.salary.AdvancePayment;

import java.time.LocalDate;

public class AdvancePaymentMapper {

    public static AdvancePaymentResponseDTO toDto(AdvancePayment advancePayment) {
        if (advancePayment == null) {
            return null;
        }
        var dto = new AdvancePaymentResponseDTO();
        dto.setId(advancePayment.getId());
        dto.setAmount(advancePayment.getAmount());
        dto.setReason(advancePayment.getReason());
        dto.setDate(advancePayment.getDate().toString());
        dto.setEmpId(advancePayment.getEmployee().getId());
        return dto;
    }

    public static AdvancePayment toEntity(AdvancePaymentRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }
        var advancePayment = new AdvancePayment();
        advancePayment.setAmount(requestDTO.getAmount());
        advancePayment.setReason(requestDTO.getReason());
        advancePayment.setDate(requestDTO.getDate());

        return advancePayment;
    }
}
