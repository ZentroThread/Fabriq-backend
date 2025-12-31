package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.salary.HolidayRequestDTO;
import com.example.FabriqBackend.dto.salary.HolidayResponseDTO;

import java.util.List;

public interface IHolidayService {

    HolidayResponseDTO addHoliday(HolidayRequestDTO holidayRequestDTO);
    List<HolidayResponseDTO> getAllHolidays();
    HolidayResponseDTO getHolidayById(int id);
    HolidayResponseDTO updateHoliday(int id, HolidayRequestDTO holidayRequestDTO);
    void deleteHoliday(int id);
    List<HolidayResponseDTO> getByDateRange(String startDate, String endDate);

}
