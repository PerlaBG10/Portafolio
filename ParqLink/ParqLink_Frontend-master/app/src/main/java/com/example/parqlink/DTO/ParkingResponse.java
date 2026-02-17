package com.example.parqlink.DTO;

public class ParkingResponse {
    private Long id;
    private String name;
    private String address;
    private double pricePerHour;
    private String nfcTagId;
    private double latitude;
    private double longitude;
    private Double distance;

    public ParkingResponse() {
    }
    public ParkingResponse(Long id, String name, String address, double pricePerHour, String nfcTagId, double latitude, double longitude, Double distance) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.pricePerHour = pricePerHour;
        this.nfcTagId = nfcTagId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getPricePerHour() { return pricePerHour; }
    public String getNfcTagId() { return nfcTagId; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParkingResponse)) return false;
        ParkingResponse that = (ParkingResponse) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}