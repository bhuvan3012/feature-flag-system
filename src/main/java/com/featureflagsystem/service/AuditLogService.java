package com.featureflagsystem.service;

import com.featureflagsystem.model.AuditLog;
import com.featureflagsystem.repository.AuditLogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(String action, String flagName, String details) {
        String username = "system";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            username = auth.getName();
        }

        AuditLog log = AuditLog.builder()
                .action(action)
                .flagName(flagName)
                .changedBy(username)
                .details(details)
                .build();
                
        auditLogRepository.save(log);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    public List<AuditLog> getLogsForFlag(String flagName) {
        return auditLogRepository.findByFlagNameOrderByTimestampDesc(flagName);
    }
}
