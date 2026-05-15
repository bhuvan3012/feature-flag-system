package com.featureflagsystem.controller;

import com.featureflagsystem.model.AuditLog;
import com.featureflagsystem.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @GetMapping("/{flagName}")
    public ResponseEntity<List<AuditLog>> getLogsForFlag(@PathVariable String flagName) {
        return ResponseEntity.ok(auditLogService.getLogsForFlag(flagName));
    }
}
