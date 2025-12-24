package com.example.FabriqBackend.scheduler;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.service.impl.AttendanceServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;


@Component
@RequiredArgsConstructor
public class AttendanceScheduler {

    private final AttendanceServiceImpl attendanceService;
    private final EmployeeDao employeeDao;

    @Scheduled(cron = "0 0 23 * * *")
    // daily at 11:00 PM  second minute hour day-of-month month day-of-week
   //@Scheduled(cron = "0 */1 * * * *")
    public void autoProcessAllTenants() {

        List<String> tenantIds = employeeDao.findAllTenantIds();

        LocalDate today = LocalDate.now();

        for (String tenantId : tenantIds) {
            try {
                TenantContext.setCurrentTenant(tenantId);

                employeeDao.findAll()
                        .forEach(emp -> attendanceService.updateDailyAttendance(emp.getEmpCode(), today));

                System.out.println("Processed attendance for tenant: " + tenantId + " on date: " + today);

            } finally {
                TenantContext.clear();
            }
        }
    }
}