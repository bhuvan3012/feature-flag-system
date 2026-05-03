package com.featureflagsystem.service;

import com.featureflagsystem.dto.EvaluationRequest;
import com.featureflagsystem.dto.EvaluationResponse;
import com.featureflagsystem.model.FeatureFlag;
import com.featureflagsystem.repository.FeatureFlagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class EvaluationServiceTest {

    @Mock
    private FeatureFlagRepository repository;

    @InjectMocks
    private EvaluationService evaluationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openInMocks(this);
    }

    @Test
    void testEvaluate_FlagDoesNotExist() {
        when(repository.findByName(anyString())).thenReturn(Optional.empty());

        EvaluationRequest req = new EvaluationRequest("testFlag", "user1", "PROD");
        EvaluationResponse res = evaluationService.evaluate(req);

        assertFalse(res.isEnabled());
        assertEquals("Flag doesn't exist", res.getReason());
    }

    @Test
    void testEvaluate_FlagDisabled() {
        FeatureFlag flag = FeatureFlag.builder().name("testFlag").enabled(false).environment("PROD").build();
        when(repository.findByName(anyString())).thenReturn(Optional.of(flag));

        EvaluationRequest req = new EvaluationRequest("testFlag", "user1", "PROD");
        EvaluationResponse res = evaluationService.evaluate(req);

        assertFalse(res.isEnabled());
        assertEquals("Flag is disabled globally", res.getReason());
    }

    @Test
    void testEvaluate_EnvironmentMismatch() {
        FeatureFlag flag = FeatureFlag.builder().name("testFlag").enabled(true).environment("STAGING").build();
        when(repository.findByName(anyString())).thenReturn(Optional.of(flag));

        EvaluationRequest req = new EvaluationRequest("testFlag", "user1", "PROD");
        EvaluationResponse res = evaluationService.evaluate(req);

        assertFalse(res.isEnabled());
        assertEquals("Environment mismatch", res.getReason());
    }

    @Test
    void testEvaluate_UserTargeted() {
        FeatureFlag flag = FeatureFlag.builder().name("testFlag").enabled(true).environment("PROD").targetUserIds("user1, user2").build();
        when(repository.findByName(anyString())).thenReturn(Optional.of(flag));

        EvaluationRequest req = new EvaluationRequest("testFlag", "user1", "PROD");
        EvaluationResponse res = evaluationService.evaluate(req);

        assertTrue(res.isEnabled());
        assertEquals("User specifically targeted", res.getReason());
    }

    @Test
    void testEvaluate_100PercentRollout() {
        FeatureFlag flag = FeatureFlag.builder().name("testFlag").enabled(true).environment("PROD").rolloutPercentage(100).build();
        when(repository.findByName(anyString())).thenReturn(Optional.of(flag));

        EvaluationRequest req = new EvaluationRequest("testFlag", "user1", "PROD");
        EvaluationResponse res = evaluationService.evaluate(req);

        assertTrue(res.isEnabled());
        assertEquals("100% rollout", res.getReason());
    }
}
