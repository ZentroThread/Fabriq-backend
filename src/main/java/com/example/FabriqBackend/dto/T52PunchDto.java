package com.example.FabriqBackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class T52PunchDto {

    @JsonProperty("UserID")
    private String userID;

    @JsonProperty("LogDate")
    private String logDate;

    @JsonProperty("Direction")
    private String direction;

    @JsonProperty("emp_code")
    private String empCode;

    @JsonProperty("punch_time")
    private String punchTime;

    @JsonProperty("status")
    private Integer status;

}
