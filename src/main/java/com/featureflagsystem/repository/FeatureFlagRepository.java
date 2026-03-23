package com.yourpackage.repository;

import com.yourpackage.model.FeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {
    Optional<FeatureFlag> findByName(String name);
}