package com.example.FabriqBackend.service.impl;

import lombok.extern.slf4j.Slf4j;

import com.example.FabriqBackend.dao.HolidayDao;
import com.example.FabriqBackend.dto.salary.HolidayRequestDTO;
import com.example.FabriqBackend.dto.salary.HolidayResponseDTO;
import com.example.FabriqBackend.exception.ResourceNotFoundException;
import com.example.FabriqBackend.model.salary.Holiday;
import com.example.FabriqBackend.service.Interface.IHolidayService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "holidays")
public class HolidayServiceImpl implements IHolidayService {

    private final HolidayDao holidayDao;
    private final ModelMapper modelMapper;

    @Override
    @CacheEvict(
        value = "holidays", 
        key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + '*'"
    )
    public HolidayResponseDTO addHoliday(HolidayRequestDTO holidayRequestDTO) {
        Holiday holiday = modelMapper.map(holidayRequestDTO, Holiday.class);
        holidayDao.save(holiday);
        return modelMapper.map(holiday, HolidayResponseDTO.class);
    }

    @Override
    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allHolidays'")
    public List<HolidayResponseDTO> getAllHolidays() {
        List<Holiday> holidays = holidayDao.findAll();
        return holidays
                .stream()
                .map(holiday -> modelMapper.map(holiday, HolidayResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public HolidayResponseDTO getHolidayById(int id) {

        Holiday holiday = holidayDao.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Holiday", "id", String.valueOf(id)));

        return modelMapper.map(holiday, HolidayResponseDTO.class);
    }

    @Override
    @CacheEvict(
        value = "holidays", 
        key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + '*'"
    )
    public HolidayResponseDTO updateHoliday(int id, HolidayRequestDTO holidayRequestDTO) {
        Holiday existingHoliday = holidayDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday", "id", String.valueOf(id)));
        Holiday updatedHoliday = modelMapper.map(holidayRequestDTO, Holiday.class);
        updatedHoliday.setId(existingHoliday.getId());
        holidayDao.save(updatedHoliday);

        return modelMapper.map(updatedHoliday, HolidayResponseDTO.class);
    }

    @Override
    @CacheEvict(
        value = "holidays", 
        key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + '*'"
    )
    public void deleteHoliday(int id) {
        if (!holidayDao.existsById(id)) {
            throw new ResourceNotFoundException("Holiday", "id", String.valueOf(id));
        }
        holidayDao.deleteById(id);
    }

    @Override
    public List<HolidayResponseDTO> getByDateRange(String startDate, String endDate) {
        List<Holiday> holidays = holidayDao.findByDateBetween(startDate, endDate);
        return holidays.stream()
                .map(holiday -> modelMapper.map(holiday, HolidayResponseDTO.class))
                .collect(Collectors.toList());
    }
}
