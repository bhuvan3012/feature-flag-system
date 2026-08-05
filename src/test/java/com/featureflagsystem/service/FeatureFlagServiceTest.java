package com.featureflagsystem.service;

import com.featureflagsystem.dto.FeatureFlagRequest;
import com.featureflagsystem.dto.FeatureFlagResponse;
import com.featureflagsystem.exception.DuplicateResourceException;
import com.featureflagsystem.exception.ResourceNotFoundException;
import com.featureflagsystem.model.FeatureFlag;
import com.featureflagsystem.repository.FeatureFlagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FeatureFlagServiceTest {

    @Mock
    private FeatureFlagRepository repository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private FeatureFlagService featureFlagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateFlag_Success() {
        FeatureFlagRequest req = FeatureFlagRequest.builder()
                .name("flag1")
                .description("Test Flag")
                .enabled(true)
                .environment("PROD")
                .rolloutPercentage(100)
                .build();

        FeatureFlag savedFlag = FeatureFlag.builder()
                .id(1L)
                .name("flag1")
                .description("Test Flag")
                .enabled(true)
                .environment("PROD")
                .rolloutPercentage(100)
                .build();

        when(repository.findByName("flag1")).thenReturn(Optional.empty());
        when(repository.save(any(FeatureFlag.class))).thenReturn(savedFlag);

        FeatureFlagResponse res = featureFlagService.createFlag(req);

        assertNotNull(res);
        assertEquals("flag1", res.getName());
        assertTrue(res.isEnabled());
        verify(auditLogService, times(1)).logAction(eq("CREATE"), eq("flag1"), anyString());
    }

    @Test
    void testCreateFlag_DuplicateNameThrowsException() {
        FeatureFlagRequest req = FeatureFlagRequest.builder().name("existingFlag").build();
        when(repository.findByName("existingFlag")).thenReturn(Optional.of(new FeatureFlag()));

        assertThrows(DuplicateResourceException.class, () -> featureFlagService.createFlag(req));
    }

    @Test
    void testGetAllFlags() {
        FeatureFlag flag1 = FeatureFlag.builder().id(1L).name("flag1").build();
        FeatureFlag flag2 = FeatureFlag.builder().id(2L).name("flag2").build();
        when(repository.findAll()).thenReturn(List.of(flag1, flag2));

        List<FeatureFlagResponse> flags = featureFlagService.getAllFlags();

        assertEquals(2, flags.size());
    }

    @Test
    void testGetFlagByName_Success() {
        FeatureFlag flag = FeatureFlag.builder().id(1L).name("flag1").build();
        when(repository.findByName("flag1")).thenReturn(Optional.of(flag));

        FeatureFlagResponse res = featureFlagService.getFlagByName("flag1");

        assertNotNull(res);
        assertEquals("flag1", res.getName());
    }

    @Test
    void testGetFlagByName_NotFoundThrowsException() {
        when(repository.findByName("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> featureFlagService.getFlagByName("unknown"));
    }

    @Test
    void testToggleFlag() {
        FeatureFlag flag = FeatureFlag.builder().id(1L).name("flag1").enabled(false).build();
        when(repository.findByName("flag1")).thenReturn(Optional.of(flag));
        when(repository.save(any(FeatureFlag.class))).thenAnswer(i -> i.getArgument(0));

        FeatureFlagResponse res = featureFlagService.toggleFlag("flag1");

        assertTrue(res.isEnabled());
        verify(auditLogService, times(1)).logAction(eq("TOGGLE"), eq("flag1"), contains("ENABLED"));
    }

    @Test
    void testDeleteFlag() {
        FeatureFlag flag = FeatureFlag.builder().id(1L).name("flag1").build();
        when(repository.findByName("flag1")).thenReturn(Optional.of(flag));

        featureFlagService.deleteFlag("flag1");

        verify(repository, times(1)).delete(flag);
        verify(auditLogService, times(1)).logAction(eq("DELETE"), eq("flag1"), anyString());
    }
}
