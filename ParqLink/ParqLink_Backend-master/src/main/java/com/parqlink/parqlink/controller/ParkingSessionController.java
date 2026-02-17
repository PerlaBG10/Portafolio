package com.parqlink.parqlink.controller;

import com.parqlink.parqlink.dto.*;
import com.parqlink.parqlink.entity.*;
import com.parqlink.parqlink.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/parking")
public class ParkingSessionController {

    private final ParkingService parkingService;
    private final ParkingSessionService sessionService;
    private final UserService userService;

    public ParkingSessionController(ParkingService parkingService,
                                    ParkingSessionService sessionService,
                                    UserService userService) {
        this.parkingService = parkingService;
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @PostMapping("/scan")
    public ParkingSessionResponse scanNfc(@RequestBody NfcScanRequest request, Authentication auth) {
        String userEmail = auth.getName();
        User user = userService.getUserByEmail(userEmail).orElseThrow();
        Parking parking = parkingService.getParkingByNfcTag(request.getNfcTagId())
                .orElseThrow(() -> new RuntimeException("Parking not found"));

        var existing = sessionService.findOngoingSession(user, parking);

        if (existing.isPresent()) {
            // End session
            ParkingSession ended = sessionService.endSession(existing.get().getId());
            return mapToDto(ended);
        } else {
            // Start session
            ParkingSession started = sessionService.startSession(user, parking);
            return mapToDto(started);
        }
    }

    private ParkingSessionResponse mapToDto(ParkingSession session) {
        ParkingSessionResponse dto = new ParkingSessionResponse();
        dto.setSessionId(session.getId());
        dto.setParkingName(session.getParking().getName());
        dto.setStartTime(session.getStartTime());
        dto.setEndTime(session.getEndTime());
        dto.setTotalCost(session.getTotalCost());
        dto.setPricePerHour(session.getParking().getPricePerHour());
        return dto;
    }

    @GetMapping("/sessions")
    public List<UserSessionResponse> getUserSessions(Authentication auth) {
        String email = auth.getName();
        User user = userService.getUserByEmail(email).orElseThrow();

        return sessionService.getSessionsForUser(user).stream().map(session -> {
            UserSessionResponse dto = new UserSessionResponse();
            dto.setSessionId(session.getId());
            dto.setParkingName(session.getParking().getName());
            dto.setStartTime(session.getStartTime());
            dto.setEndTime(session.getEndTime());
            dto.setTotalCost(session.getTotalCost());
            dto.setAddress(session.getParking().getAddress());
            dto.setPricePerHour(session.getParking().getPricePerHour());

            if (session.getEndTime() != null) {
                Duration duration = Duration.between(session.getStartTime(), session.getEndTime());
                long hours = duration.toHours();
                long minutes = duration.toMinutes() % 60;
                dto.setDuration(hours + "h " + minutes + "m");
            } else {
                dto.setDuration("Ongoing");
            }

            return dto;
        }).toList();
    }

}
