package com.example.FabriqBackend.service.Interface.Ipayroll;

import com.example.FabriqBackend.dto.salary.EpfFormDTO;

import java.util.List;

public interface EpfReportService {
    List<EpfFormDTO> getEpfForm(int month, int year);
}
