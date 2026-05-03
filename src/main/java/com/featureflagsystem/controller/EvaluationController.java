package com.featureflagsystem.controller;

import com.featureflagsystem.dto.EvaluationRequest;
import com.featureflagsystem.dto.EvaluationResponse;
import com.featureflagsystem.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/evaluate")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping
    public ResponseEntity<EvaluationResponse> evaluate(@Valid @RequestBody EvaluationRequest request) {
        return ResponseEntity.ok(evaluationService.evaluate(request));
    }

    @GetMapping("/{flagName}")
    public ResponseEntity<EvaluationResponse> evaluateGet(
            @PathVariable String flagName,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "PROD") String environment) {
        
        EvaluationRequest req = new EvaluationRequest(flagName, userId, environment);
        return ResponseEntity.ok(evaluationService.evaluate(req));
    }
}
