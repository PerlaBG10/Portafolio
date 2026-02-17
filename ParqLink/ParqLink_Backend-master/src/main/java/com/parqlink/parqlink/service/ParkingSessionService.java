package com.parqlink.parqlink.service;

import com.parqlink.parqlink.entity.Parking;
import com.parqlink.parqlink.entity.ParkingSession;
import com.parqlink.parqlink.entity.User;
import com.parqlink.parqlink.repository.ParkingSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingSessionService {
    private final ParkingSessionRepository sessionRepository;

    public ParkingSessionService(ParkingSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public ParkingSession startSession(User user, Parking parking) {
        ParkingSession session = new ParkingSession();
        session.setUser(user);
        session.setParking(parking);
        session.setStartTime(LocalDateTime.now());
        session.setTotalCost(0.0);
        return sessionRepository.save(session);
    }

    public ParkingSession endSession(Long sessionId) {
        Optional<ParkingSession> opt = sessionRepository.findById(sessionId);
        if (opt.isEmpty()) throw new RuntimeException("Session not found");

        ParkingSession session = opt.get();
        session.setEndTime(LocalDateTime.now());

        long durationSeconds = java.time.Duration.between(session.getStartTime(), session.getEndTime()).getSeconds();
        long billedHours = Math.max(1, (long) Math.ceil(durationSeconds / 3600.0));

        double rate = session.getParking().getPricePerHour();
        session.setTotalCost(billedHours * rate);

        return sessionRepository.save(session);
    }


    public List<ParkingSession> getUserSessions(Long userId) {
        return sessionRepository.findByUserId(userId);
    }

    public Optional<ParkingSession> findOngoingSession(User user, Parking parking) {
        return sessionRepository.findByUserId(user.getId()).stream()
                .filter(session -> session.getParking().getId().equals(parking.getId()) && session.getEndTime() == null)
                .findFirst();
    }

    public List<ParkingSession> getSessionsForUser(User user) {
        return sessionRepository.findByUserId(user.getId());
    }
}
