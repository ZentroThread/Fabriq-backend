package com.example.FabriqBackend.enums;

import lombok.Getter;

@Getter
public enum AttendanceStatus {
    PRESENT(0.0),
    ABSENT(1.0),
    LATE(0.0),
    EARLY_LEAVE(0.0),
    HALF_DAY(0.5);

    private final double dayValue;

    AttendanceStatus(double dayValue) {
        this.dayValue = dayValue;
    }

}
