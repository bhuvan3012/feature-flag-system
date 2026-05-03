package com.featureflagsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.featureflagsystem.dto.FeatureFlagRequest;
import com.featureflagsystem.dto.FeatureFlagResponse;
import com.featureflagsystem.service.FeatureFlagService;
import com.featureflagsystem.utils.JwtUtil;
import com.featureflagsystem.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeatureFlagController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for simple controller unit test
class FeatureFlagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeatureFlagService featureFlagService;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateFlag_Success() throws Exception {
        FeatureFlagRequest request = FeatureFlagRequest.builder()
                .name("newFlag")
                .enabled(true)
                .rolloutPercentage(50)
                .build();

        FeatureFlagResponse response = FeatureFlagResponse.builder()
                .id(1L)
                .name("newFlag")
                .enabled(true)
                .rolloutPercentage(50)
                .createdAt(LocalDateTime.now())
                .build();

        when(featureFlagService.createFlag(any(FeatureFlagRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("newFlag"))
                .andExpect(jsonPath("$.enabled").value(true));
    }
}
