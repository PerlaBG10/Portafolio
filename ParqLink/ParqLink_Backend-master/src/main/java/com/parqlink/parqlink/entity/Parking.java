package com.parqlink.parqlink.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Parking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    @Column(nullable = false)
    private double latitude;
    @Column(nullable = false)
    private double longitude;
    private double pricePerHour;
    private String nfcTagId;

    @OneToMany(mappedBy = "parking", cascade = CascadeType.ALL)
    private List<ParkingSession> sessions;
}

