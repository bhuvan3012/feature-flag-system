package com.featureflagsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureFlagResponse {
    private Long id;
    private String name;
    private boolean enabled;
    private String description;
    private String environment;
    private int rolloutPercentage;
    private String targetUserIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
