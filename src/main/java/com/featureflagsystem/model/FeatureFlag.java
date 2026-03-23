package com.yourpackage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feature_flags")
public class FeatureFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private boolean enabled;

    private String description;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters & Setters
}