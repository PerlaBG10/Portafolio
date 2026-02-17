package com.parqlink.parqlink.controller;

import com.parqlink.parqlink.dto.ParkingResponse;
import com.parqlink.parqlink.entity.Parking;
import com.parqlink.parqlink.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    @GetMapping("/all")
    public List<ParkingResponse> getFilteredAndSortedParkings(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double maxDistance,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false, defaultValue = "distance") String sortBy,     // "distance" or "price"
            @RequestParam(required = false, defaultValue = "asc") String order,       // "asc" or "desc"
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "20", required = false) int size
    ) {
        // Inline record for pairing parking with distance
        record ParkingWithDistance(Parking parking, Double distance) {}

        List<Parking> all = parkingService.getAllParkings();

        // Filter and calculate distance
        List<ParkingWithDistance> filtered = all.stream()
                .filter(p -> {
                    boolean matchesPrice = maxPrice == null || p.getPricePerHour() <= maxPrice;
                    boolean matchesName = name == null || p.getName().toLowerCase().contains(name.toLowerCase());
                    boolean matchesAddress = address == null || p.getAddress().toLowerCase().contains(address.toLowerCase());
                    return matchesPrice && matchesName && matchesAddress;
                })
                .map(p -> {
                    Double distance = (lat != null && lng != null)
                            ? haversine(lat, lng, p.getLatitude(), p.getLongitude())
                            : null;
                    return new ParkingWithDistance(p, distance);
                })
                .filter(pwd -> {
                    if (maxDistance == null || pwd.distance == null) return true;
                    return pwd.distance <= maxDistance;
                })
                .toList();

        // Sorting
        if ("price".equalsIgnoreCase(sortBy)) {
            if ("desc".equalsIgnoreCase(order)) {
                filtered = filtered.stream()
                        .sorted(Comparator.comparingDouble(pwd -> -pwd.parking().getPricePerHour()))
                        .toList();
            } else {
                filtered = filtered.stream()
                        .sorted(Comparator.comparingDouble(pwd -> pwd.parking().getPricePerHour()))
                        .toList();
            }
        } else if ("distance".equalsIgnoreCase(sortBy)) {
            if (lat != null && lng != null) {
                if ("desc".equalsIgnoreCase(order)) {
                    filtered = filtered.stream()
                            .sorted(Comparator.comparingDouble(pwd -> -pwd.distance()))
                            .toList();
                } else {
                    filtered = filtered.stream()
                            .sorted(Comparator.comparingDouble(ParkingWithDistance::distance))
                            .toList();
                }
            }
        }

        // Map to response DTO
        return filtered.stream()
                .skip((long) page * size)
                .limit(size)
                .map(pwd -> {
                    Parking p = pwd.parking();
                    ParkingResponse dto = new ParkingResponse();
                    dto.setId(p.getId());
                    dto.setName(p.getName());
                    dto.setAddress(p.getAddress());
                    dto.setPricePerHour(p.getPricePerHour());
                    dto.setNfcTagId(p.getNfcTagId());
                    dto.setLatitude(p.getLatitude());
                    dto.setLongitude(p.getLongitude());
                    dto.setDistance(pwd.distance());
                    return dto;
                }).toList();
    }


    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

}

