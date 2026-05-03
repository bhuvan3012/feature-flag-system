package com.featureflagsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {
    private String flagName;
    private boolean enabled;
    private String reason;
}
