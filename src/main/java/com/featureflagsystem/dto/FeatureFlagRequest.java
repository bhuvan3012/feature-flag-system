package com.featureflagsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureFlagRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private boolean enabled;

    private String description;

    private String environment = "PROD";

    @Min(value = 0, message = "Rollout percentage must be at least 0")
    @Max(value = 100, message = "Rollout percentage cannot exceed 100")
    private int rolloutPercentage = 100;

    private String targetUserIds;
}
