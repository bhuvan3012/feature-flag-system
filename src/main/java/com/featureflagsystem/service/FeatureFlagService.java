package com.featureflagsystem.service;

import com.featureflagsystem.dto.FeatureFlagRequest;
import com.featureflagsystem.dto.FeatureFlagResponse;
import com.featureflagsystem.exception.DuplicateResourceException;
import com.featureflagsystem.exception.ResourceNotFoundException;
import com.featureflagsystem.model.FeatureFlag;
import com.featureflagsystem.repository.FeatureFlagRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeatureFlagService {

    private final FeatureFlagRepository repository;
    private final AuditLogService auditLogService;

    public FeatureFlagService(FeatureFlagRepository repository, AuditLogService auditLogService) {
        this.repository = repository;
        this.auditLogService = auditLogService;
    }

    public FeatureFlagResponse createFlag(FeatureFlagRequest request) {
        if (repository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("Feature flag with name '" + request.getName() + "' already exists");
        }
        
        FeatureFlag flag = FeatureFlag.builder()
                .name(request.getName())
                .enabled(request.isEnabled())
                .description(request.getDescription())
                .environment(request.getEnvironment())
                .rolloutPercentage(request.getRolloutPercentage())
                .targetUserIds(request.getTargetUserIds())
                .build();
                
        FeatureFlag saved = repository.save(flag);
        auditLogService.logAction("CREATE", saved.getName(), "Created flag with " + saved.getRolloutPercentage() + "% rollout in " + saved.getEnvironment());
        return mapToResponse(saved);
    }

    public List<FeatureFlagResponse> getAllFlags() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "featureFlags", key = "#name")
    public FeatureFlagResponse getFlagByName(String name) {
        FeatureFlag flag = repository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Flag '" + name + "' not found"));
        return mapToResponse(flag);
    }

    @CacheEvict(value = "featureFlags", key = "#name")
    public FeatureFlagResponse updateFlag(String name, FeatureFlagRequest request) {
        FeatureFlag existing = repository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Flag '" + name + "' not found"));
                
        existing.setEnabled(request.isEnabled());
        existing.setDescription(request.getDescription());
        existing.setEnvironment(request.getEnvironment());
        existing.setRolloutPercentage(request.getRolloutPercentage());
        existing.setTargetUserIds(request.getTargetUserIds());
        
        FeatureFlag updated = repository.save(existing);
        auditLogService.logAction("UPDATE", updated.getName(), "Updated flag configuration");
        return mapToResponse(updated);
    }

    @CacheEvict(value = "featureFlags", key = "#name")
    public void deleteFlag(String name) {
        FeatureFlag existing = repository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Flag '" + name + "' not found"));
        repository.delete(existing);
        auditLogService.logAction("DELETE", name, "Deleted flag");
    }

    @CacheEvict(value = "featureFlags", key = "#name")
    public FeatureFlagResponse toggleFlag(String name) {
        FeatureFlag existing = repository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Flag '" + name + "' not found"));
                
        existing.setEnabled(!existing.isEnabled());
        
        FeatureFlag updated = repository.save(existing);
        auditLogService.logAction("TOGGLE", updated.getName(), "Toggled flag to: " + (updated.isEnabled() ? "ENABLED" : "DISABLED"));
        return mapToResponse(updated);
    }
    
    private FeatureFlagResponse mapToResponse(FeatureFlag flag) {
        return FeatureFlagResponse.builder()
                .id(flag.getId())
                .name(flag.getName())
                .enabled(flag.isEnabled())
                .description(flag.getDescription())
                .environment(flag.getEnvironment())
                .rolloutPercentage(flag.getRolloutPercentage())
                .targetUserIds(flag.getTargetUserIds())
                .createdAt(flag.getCreatedAt())
                .updatedAt(flag.getUpdatedAt())
                .build();
    }
}