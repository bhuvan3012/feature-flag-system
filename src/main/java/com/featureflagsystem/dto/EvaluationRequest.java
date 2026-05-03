package com.featureflagsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequest {
    
    @NotBlank(message = "Flag name is required")
    private String flagName;
    
    private String userId;
    
    private String environment = "PROD";
}
