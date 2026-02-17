package com.parqlink.parqlink.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParkingSessionResponse {
    private Long sessionId;
    private String parkingName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalCost;
    private Double pricePerHour;
}
