package com.parqlink.parqlink.repository;

import com.parqlink.parqlink.entity.Parking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingRepository extends JpaRepository<Parking, Long> {
    Optional<Parking> findByNfcTagId(String nfcTagId);
}

