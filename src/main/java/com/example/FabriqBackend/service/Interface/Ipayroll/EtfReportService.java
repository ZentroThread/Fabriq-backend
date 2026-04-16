package com.example.FabriqBackend.service.Interface.Ipayroll;

import com.example.FabriqBackend.dto.salary.EtfFormDTO;

import java.util.List;

public interface EtfReportService {
    List<EtfFormDTO> getEtfForm(int month, int year);
}
