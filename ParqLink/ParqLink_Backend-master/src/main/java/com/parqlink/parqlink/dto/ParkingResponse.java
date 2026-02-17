package com.parqlink.parqlink.dto;

import lombok.Data;

@Data
public class ParkingResponse {
    private Long id;
    private String name;
    private String address;
    private double pricePerHour;
    private String nfcTagId;
    private double latitude;
    private double longitude;
    private Double distance;
}
