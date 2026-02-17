package com.parqlink.parqlink.service;

import com.parqlink.parqlink.entity.Parking;
import com.parqlink.parqlink.repository.ParkingRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ParkingService {
    private final ParkingRepository parkingRepository;

    public Optional<Parking> getParkingByNfcTag(String nfcTagId) {
        return parkingRepository.findByNfcTagId(nfcTagId);
    }

    public Parking saveParking(Parking parking) {
        return parkingRepository.save(parking);
    }

    @Cacheable("parkings")
    public List<Parking> getAllParkings() {
        return parkingRepository.findAll();
    }

}
