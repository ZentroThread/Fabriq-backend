package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.AppConfig;
import com.example.FabriqBackend.dao.AttendanceDao;
import com.example.FabriqBackend.dto.AttendanceDto;
import com.example.FabriqBackend.model.Attendance;
import com.example.FabriqBackend.service.IAttendanceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AttendanceService implements IAttendanceService {
    private final AttendanceDao attendanceDao;
    private final AppConfig appConfig;

    public void markAttendance(AttendanceDto dto){
        if (dto.getEmpCode() == null || dto.getEmpCode().isEmpty()) {
            throw new RuntimeException("empCode is required to mark attendance");
        }
        Attendance attendance = appConfig.modelMapper().map(dto,Attendance.class);
        attendanceDao.save(attendance);
    }

//    public List<AttendanceDto> fetchAttendanceByEmpCodeAndDate(String empCode,String date ){
//
//        Optional<List<Attendance>> attendanceList = attendanceDao.findByEmpCodeAndDate(empCode, LocalDate.parse(date));
//        if(attendanceList.isEmpty()){
//            throw new RuntimeException("Attendance not found for empCode:" + empCode + "and date: " + date);
//        }
//        return attendanceList
//                .stream()
//                .map(attendance-> appConfig.modelMapper().map(attendance,AttendanceDto.class))
//                .toList();
//    }

    //get attendance for an employee for a month
    public List<AttendanceDto> getMonthlyAttendanceByEmpCode(String empCode,int year,int month){

        LocalDate start = LocalDate.of(year,month,1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        Optional<List<Attendance>> attendanceList = attendanceDao.findByEmpCodeAndDateBetweenOrderByTimeAsc(empCode,start,end);
        if(attendanceList.isEmpty()){
            throw new RuntimeException("Attendance not found for empCode:" + empCode + "for month: " + month + " and year: " + year);
        }
        return attendanceList.get()
                .stream()
                .map(attendance-> appConfig.modelMapper().map(attendance,AttendanceDto.class))
                .toList();

    }

    //get attendance for all employees for a month
    public List<AttendanceDto> fetchAllAttendanceForMonth(int year, int month){

        LocalDate start = LocalDate.of(year,month,1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        Optional<List<Attendance>> attendanceList = attendanceDao.findByDateBetweenOrderByTimeAsc(start,end);

        if(attendanceList.isEmpty()){
            throw new RuntimeException("Attendance not found for month: " + month + " and year: " + year);
        }
        return attendanceList.get()
                .stream()
                .map(attendance-> appConfig.modelMapper().map(attendance,AttendanceDto.class))
                .toList();
    }

    //get attendance for all employees for a date
    public List<AttendanceDto> fetchAllAttendanceForDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        Optional<List<AttendanceDto>> attendanceList = attendanceDao.findByDate(localDate)
                .map(attendances -> attendances.stream()
                        .map(attendance -> appConfig.modelMapper().map(attendance, AttendanceDto.class))
                        .toList());

        if (attendanceList.isEmpty()){
            throw new RuntimeException("Attendance not found for date: " + date);
        } else {
            return attendanceList.get();
        }
    }
}
