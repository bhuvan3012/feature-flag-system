package com.featureflagsystem.service;

import com.featureflagsystem.dto.EvaluationRequest;
import com.featureflagsystem.dto.EvaluationResponse;
import com.featureflagsystem.model.FeatureFlag;
import com.featureflagsystem.repository.FeatureFlagRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Service
public class EvaluationService {

    private final FeatureFlagRepository repository;

    public EvaluationService(FeatureFlagRepository repository) {
        this.repository = repository;
    }

    public EvaluationResponse evaluate(EvaluationRequest request) {
        FeatureFlag flag = repository.findByName(request.getFlagName())
                .orElse(null);

        if (flag == null) {
            return new EvaluationResponse(request.getFlagName(), false, "Flag doesn't exist");
        }

        if (!flag.isEnabled()) {
            return new EvaluationResponse(request.getFlagName(), false, "Flag is disabled globally");
        }

        if (!flag.getEnvironment().equalsIgnoreCase(request.getEnvironment())) {
            return new EvaluationResponse(request.getFlagName(), false, "Environment mismatch");
        }

        if (flag.getTargetUserIds() != null && !flag.getTargetUserIds().isEmpty() && request.getUserId() != null) {
            boolean userMatched = Arrays.stream(flag.getTargetUserIds().split(","))
                    .map(String::trim)
                    .anyMatch(id -> id.equals(request.getUserId()));
            if (userMatched) {
                return new EvaluationResponse(request.getFlagName(), true, "User specifically targeted");
            }
        }

        if (flag.getRolloutPercentage() == 100) {
            return new EvaluationResponse(request.getFlagName(), true, "100% rollout");
        }

        if (flag.getRolloutPercentage() == 0) {
            return new EvaluationResponse(request.getFlagName(), false, "0% rollout");
        }
        
        if (request.getUserId() == null || request.getUserId().isEmpty()) {
            return new EvaluationResponse(request.getFlagName(), false, "User ID required for percentage rollout");
        }

        boolean fitsRollout = isUserInRollout(request.getUserId(), request.getFlagName(), flag.getRolloutPercentage());
        
        if (fitsRollout) {
            return new EvaluationResponse(request.getFlagName(), true, "User falls within rollout percentage");
        } else {
            return new EvaluationResponse(request.getFlagName(), false, "User falls outside rollout percentage");
        }
    }

    private boolean isUserInRollout(String userId, String flagName, int percentage) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest((userId + "-" + flagName).getBytes(StandardCharsets.UTF_8));
            // Just take first 4 bytes for integer
            int hashInt = ((hashBytes[0] & 0xFF) << 24) |
                          ((hashBytes[1] & 0xFF) << 16) |
                          ((hashBytes[2] & 0xFF) << 8) |
                          (hashBytes[3] & 0xFF);
            
            int normalized = Math.abs(hashInt) % 100;
            return normalized < percentage;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not available", e);
        }
    }
}
