package com.parqlink.parqlink.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String role; // USER or ADMIN

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ParkingSession> sessions;
}

