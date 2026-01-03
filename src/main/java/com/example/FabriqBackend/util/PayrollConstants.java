package com.example.FabriqBackend.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PayrollConstants {

    public static final double EPF_EMPLOYEE_RATE = 0.08;
    public static final double EPF_EMPLOYER_RATE = 0.12;
    public static final double ETF_RATE = 0.03;
    public static final double SINGLE_OVERTIME_RATE = 1.5;
    public static final double DOUBLE_OVERTIME_RATE = 2.0;

    public static final int STANDARD_WORKING_HOURS = 160;
    public static final int STANDARD_WORKING_DAYS = 25;
    public static final int STANDARD_WORKING_HOURS_IN_DAY = 9;

    public static final int DEFAULT_MONTHLY_HOLIDAYS = 5;
    public static final double BREAK_TIME_HOURS = 1.0;
}
