package com.parqlink.parqlink.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserSessionResponse {
    private Long sessionId;
    private String parkingName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double totalCost;
    private String duration;
    private String address;
    private double pricePerHour;
}

