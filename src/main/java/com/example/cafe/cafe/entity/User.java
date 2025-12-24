package com.example.cafe.cafe.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;          // LOGIN ID

    @Column(nullable = false)
    private String password;       // BCrypt hash

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private Long branchId;

    private boolean firstLogin = true;

    private boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();


}
