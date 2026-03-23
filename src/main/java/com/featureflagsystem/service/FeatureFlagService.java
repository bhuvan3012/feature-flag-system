package com.yourpackage.service;

import com.yourpackage.model.FeatureFlag;
import com.yourpackage.repository.FeatureFlagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeatureFlagService {

    private final FeatureFlagRepository repository;

    public FeatureFlagService(FeatureFlagRepository repository) {
        this.repository = repository;
    }

    public FeatureFlag createFlag(FeatureFlag flag) {
        return repository.save(flag);
    }

    public List<FeatureFlag> getAllFlags() {
        return repository.findAll();
    }

    public FeatureFlag getFlagByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Flag not found"));
    }

    public FeatureFlag updateFlag(String name, FeatureFlag updated) {
        FeatureFlag existing = getFlagByName(name);
        existing.setEnabled(updated.isEnabled());
        existing.setDescription(updated.getDescription());
        return repository.save(existing);
    }

    public void deleteFlag(String name) {
        FeatureFlag flag = getFlagByName(name);
        repository.delete(flag);
    }
}