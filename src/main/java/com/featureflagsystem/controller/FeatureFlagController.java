package com.featureflagsystem.controller;

import com.featureflagsystem.dto.FeatureFlagRequest;
import com.featureflagsystem.dto.FeatureFlagResponse;
import com.featureflagsystem.service.FeatureFlagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flags")
public class FeatureFlagController {

    private final FeatureFlagService service;

    public FeatureFlagController(FeatureFlagService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FeatureFlagResponse> create(@Valid @RequestBody FeatureFlagRequest request) {
        return new ResponseEntity<>(service.createFlag(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FeatureFlagResponse>> getAll() {
        return ResponseEntity.ok(service.getAllFlags());
    }

    @GetMapping("/{name}")
    public ResponseEntity<FeatureFlagResponse> getByName(@PathVariable String name) {
        return ResponseEntity.ok(service.getFlagByName(name));
    }

    @PutMapping("/{name}")
    public ResponseEntity<FeatureFlagResponse> update(@PathVariable String name, @Valid @RequestBody FeatureFlagRequest request) {
        return ResponseEntity.ok(service.updateFlag(name, request));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> delete(@PathVariable String name) {
        service.deleteFlag(name);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PutMapping("/{name}/toggle")
    public ResponseEntity<FeatureFlagResponse> toggle(@PathVariable String name) {
        return ResponseEntity.ok(service.toggleFlag(name));
    }
}