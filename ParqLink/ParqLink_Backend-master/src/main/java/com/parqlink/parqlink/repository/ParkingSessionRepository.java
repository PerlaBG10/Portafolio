package com.parqlink.parqlink.repository;

import com.parqlink.parqlink.entity.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {
    List<ParkingSession> findByUserId(Long userId);
    List<ParkingSession> findByParkingId(Long parkingId);
}
